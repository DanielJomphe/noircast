(ns noircast.views.welcome
  (:require [noircast.views.common :as common])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpage "/" []
  (common/layout))
