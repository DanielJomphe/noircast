(ns noircast.views.status
  (:require [noircast.views.common :as common]
            [noir.validation :as vali]
            [noir.response   :as resp]
            [noir.session    :as session]
            [noir.cookies    :as cookies])
  (:use [clojure.string :only [trim]]
        noir.core
        hiccup.core
        hiccup.form
        hiccup.page))

(def empty-status {:id    ""
                   :name  ""
                   :host  ""
                   :state ""})

(def status-self (atom empty-status))   ; in-memory database ;)

(defn valid-name? [{:keys [name]}]
  (vali/rule (vali/min-length? name 5)
             [:name "The name must have 5 letters or more."])
  (not (vali/errors? :name)))

(defpartial error-item [[first-error]]
  [:p.help-inline.error first-error])

(defn add-attrs
  ([tag-vector attr value]
     (assoc-in  tag-vector [1 attr] value))
  ([tag-vector m]
     (update-in tag-vector [1] merge m)))

(defn control-group-tag                 ; TODO this is lame; rework
  [tag no-error? success?]
  (keyword (str tag ".control-group" (if no-error?
                                       (if success?
                                         ".success"
                                         "")
                                       ".error"))))

(defpartial status-fields [{:keys [name]} & [flash-msg]]
  ;;Keep this around for some time...
  ;;<input id="cur-name" name="cur-name" type="hidden" value="{$memberName}">
  [(control-group-tag "div"
                      (valid-name? {:name name})
                      (and (not-empty (first flash-msg))))
   [:label.control-label {:for "name"} "Server name: "]
   [:div.controls
    [:div.input-append
     (-> (text-field {:tabindex 1} "name" name)
         (add-attrs {:placeholder "Nouveau nom"
                     :required    true}))
     [:button.btn {:type "submit" :tabindex 2}
      [:i.icon-pencil] " Renommer"]]
    (vali/on-error :name error-item)
    (when (not-empty (first flash-msg))
      [:span.help-inline flash-msg])]])

(defpartial statuses [status & flash]
  [:section.self
   (status-fields status flash)]
  [:section.others
   (comment                             ;choose looping strategy
     (map #([:section.other (status-fields-other %)]) status-others)
     (for [o status-others]
       [:section.other (status-fields-other %)]))
   ])

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
   since the cookie comes from the user and isn't signed (for now)."
  [k]
  (sanitize (cookies/get k)))

(defn save-status-val!
  "Saves the value for k in status-self and the cookie.
   Provides no durability guarantees for now.
   Returns a truthy value on success."
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
  "Returns v if it's not empty. Otherwise,
   Updates status-self with a value for k restored from the cookie.
   If no such value was found in the cookie, leaves status-self untouched.
   Returns the value successfully restored, or nil."
  [k v]
  (if (not-empty v) v (restore-status-val! k)))

(defn setup-state [m]
  (let [sm (sanitize-map m)
        s (if (not-empty sm) sm empty-status)]
    (update-map-fkv restore-status-val-if-empty! s)))

;;; Controllers
(defpage "/status" {:as params}
  (let [s (setup-state params)
        f (form-to [:post "/status"]
                   (statuses s (session/flash-get :status-name)))]
    (common/layout
     [:section#status
      [:header [:h1 "Status"]]
      (assoc f 0 :form.form-horizontal)])))

(defpage [:post "/status"] {:as params}
  (let [s    (setup-state params)
        cur  (restore-status-val-if-empty! :name (:name @status-self))
        next (:name params)]
    (when (and (valid-name? params)
               (not (= cur next))
               (save-status-val! :name next))
      (session/flash-put! :status-name
                          (str "Name changed (" cur " -> " next ")!")))
    (render "/status" (assoc s :name next))))
