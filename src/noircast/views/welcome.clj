(ns noircast.views.welcome
  (:require [noircast.views.common :as common])
  (:use noir.core
        hiccup.core
        hiccup.page))

(defpage "/" []
  (common/layout [:h1 "See the Status page."]))
