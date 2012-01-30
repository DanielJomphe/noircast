(ns noircast.views.common
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpartial header-status []
  [:section#status
   [:div#debug]
   [:div#wsMessages]
   "STATUS"])

(defn nav-menu [items]
  [:nav (unordered-list (map (fn [[url text]] (link-to url text))
                             items))])

(defn nav-menu-item
  ([simple-name]
     [(str "/" simple-name) simple-name])
  ([name path]
     [name path]))

;;; TODO somehow reuse the routing table to automate this?
;;; Doing so would probably imply more work and complexity than this.
(defpartial header-menu []
  (nav-menu [(nav-menu-item "/" "home")
             (nav-menu-item "status")
             ]))

(defpartial footer-menu []
  (nav-menu []))

(defpartial layout [& content]
  (html5 {:lang "fr"} ;; TODO make sure lang is useful; configure
   [:head
    ;; TODO make sure server and ring middleware don't override this,
    ;; especially on Windows. If present in HTTP headers, the value
    ;; should be exactly 'Content-Type: text/html; charset="utf-8"'.
    ;; Let's evaluate Noir's wrap-utf8 middleware to deprecate this line
    [:meta {:charset "utf-8"}]
    [:title "noircast"]                 ;TODO configure
    ;; TODO decide: Html5Boilerplate and/or Twitter's Bootstrap
    (include-css "/css/reset.css")
    (include-js "/cljs/bootstrap.js")]
   [:body
    [:header [:h1 "HEADER"]
     (header-status)
     (header-menu)
     [:hr]]
    [:div#wrapper
     content]
    [:footer
     [:hr]
     (footer-menu)
     [:h1 "FOOTER"]]]))
