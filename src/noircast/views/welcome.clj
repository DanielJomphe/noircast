(ns noircast.views.welcome
  (:require [noircast.views.common :as common])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpage "/" []
  (common/layout
   (link-to "/welcome" "Welcome")))

(defpage "/welcome" []
  (common/layout
   [:h1 "Welcome to NoirCast!"]
   [:p  "Hope you like it."]))
