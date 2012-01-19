(ns noircast.views.common
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpartial header-status []
  [:section#status
   [:div#debug]
   [:div#wsMessages]
   "STATUS"])

(defpartial nav-menu [items]
  [:nav [:ul (map
              (fn [[url text]] [:li (link-to url text)])
              items)]])

(defpartial header-menu []
  (nav-menu [["/"        "home"]
             ]))

(defpartial footer-menu []
  (nav-menu []))

(defpartial layout [& content]
  (html5
   [:head
    ;; TODO make sure this is useful; configure
    {:lang "fr"}
    ;; TODO make sure server and ring middleware don't override this,
    ;; especially on Windows. If present in HTTP headers, the value
    ;; should be exactly 'Content-Type: text/html; charset="utf-8"'
    ;; TODO evaluate Noir's wrap-utf8 middleware to deprecate this line
    [:meta {:charset "utf-8"}]
    [:title "noircast"]
    ;; TODO use Html5Boilerplate and/or Twitter's Bootstrap
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
