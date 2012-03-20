(ns noircast.views.common
  (:use noir.core
        hiccup.core
        hiccup.page
        hiccup.element))

(defpartial header-status []
  [:section#status
   [:div#debug]
   [:div#sync-messages]
   "STATUS"])

(defn transform-link-vector [f [url text]]
  (f url text))

(defn map-link-vectors [f link-vectors]
  (map #(transform-link-vector f %) link-vectors))

(defn nav-menu [items]
  [:nav (unordered-list (map-link-vectors link-to items))])

(defn nav-menu-item
  "Defines a Nav Menu Item as a vector pair of [URL Text].
   Let's call this representation a link vector."
  ([name]
     [(str "/" name) name])
  ([name path]
     [name path]))

(defpartial header-menu []
  (nav-menu [(nav-menu-item "/" "home")
             (nav-menu-item "status")
             ]))

(defpartial footer-menu []
  (nav-menu []))

(defpartial layout [& content]
  (html5 {:lang "fr"}                   ; TODO configure
   [:head
    ;; TODO implement automated tests to make sure server and
    ;; middleware don't ever override this,
    ;; especially on Windows. If present in HTTP headers, the value
    ;; should be exactly 'Content-Type: text/html; charset="utf-8"'.
    ;; Ring now automatically sets that Content-Type, so that should
    ;; be fine.
    [:meta {:charset "utf-8"}]
    [:title "noircast"]                 ;TODO configure
    ;; TODO use Twitter Bootstrap
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
