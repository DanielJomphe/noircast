(ns noircast.views.status
  (:require [noircast.views.common :as common]
            [noir.validation :as vali]
            [noir.response   :as resp]
            [noir.session    :as session]
            [noir.cookies    :as cookies])
  (:use [clojure.string :only [trim]]
        noir.core
        hiccup.core
        hiccup.form-helpers
        hiccup.page-helpers))

(def empty-status {:id    ""
                   :name  ""
                   :host  ""
                   :state ""})

(def status-self (atom empty-status))

(def status-others (atom [empty-status
                          empty-status]))

(defn valid-name? [{:keys [name]}]
  (vali/rule (vali/min-length? name 5)
             [:name "The name must have 5 letters or more."])
  (not (vali/errors? :name)))

(defpartial error-item [[first-error]]
  [:p.error first-error])

(defpartial status-fields [{:keys [name]} & [flash-msg]]
  (vali/on-error :name error-item)
  (when (not-empty flash-msg) [:p flash-msg])
  (label      "name" "Server name: ")
  (text-field "name" name)
  (submit-button "Rename"))

(defpartial statuses [status & flash]
  [:section#instances
   [:header [:h1 "État global du système"]]
   [:div (status-fields status flash)]
   (comment
     (map #([:div (status-fields %)]) status-others))])

;;; Data structure utils
;;; TODO find standard stuff that does this or move this elsewhere
(defn update-map-fk [f m]
  (reduce (fn [a [k v]] (assoc a k (f k))) m m))

(defn update-map-fv [f m]
  (reduce (fn [a [k v]] (assoc a k (f v))) m m))

(defn update-map-fkv [f m]
  (reduce (fn [a [k v]] (assoc a k (f k v))) m m))

;;; User data utils
;;; TODO find a library that does this or move this elsewhere
(defn sanitize [s]
  (if (empty? s) "" (trim s)))

(defn sanitize-map [m]
  (update-map-fv sanitize m))

;;; State utils
(defn get-cookie
  "Finds the value for k in the cookie, and returns it sanitized
   since the cookie comes from the user."
  [k]
  (sanitize (cookies/get k)))

(defn save-status-val!
  "Saves the value for k in status-self and the cookie.
   Provides no durability guarantees for now.
   Returns v."
  [k v]
  (cookies/put!            k v)
  (swap! status-self assoc k v))

(defn restore-status-val!
  "Updates status-self with a value for k restored from the cookie.
   If no such value was found in the cookie, leaves status-self untouched.
   Returns the value successfully restored, or nil."
  [k]
  (when-let [v (get-cookie k)]
    (k (swap! status-self assoc k v))))

(defn restore-status-val-if-empty!
  ;; TODO find a way to not duplicate parts of documentation.
  "If v is not empty, returns v. Otherwise,
   Updates status-self with a value for k restored from the cookie.
   If no such value was found in the cookie, leaves status-self untouched.
   Returns the value successfully restored, or nil."
  [k v]
  (if (not-empty v) v (restore-status-val! k)))

(defn setup-state [m]
  (let [sm (sanitize-map m)
        s (if (not-empty sm) sm empty-status)]
    (update-map-fkv restore-status-val-if-empty! s)))

(defpage [:get "/status"] {:as status}
  (let [s (setup-state status)]
    (common/layout
     (form-to [:post "/status"]
              (statuses s (session/flash-get))))))

(defpage [:post "/status"] {:as status}
  (let [s    (setup-state status)
        cur  (restore-status-val-if-empty! :name (:name @status-self))
        next (:name s)]
    (when (and (valid-name? s)
               (not (= cur next)))
      (save-status-val! :name next)
      (session/flash-put! (str "Name changed (" cur " -> " next ")!")))
    (render "/status" (assoc s :name next))))
