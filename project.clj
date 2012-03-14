(defproject noircast "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [noir "1.3.0-alpha10"]
                 ]
  :jvm-opts ["-agentlib:jdwp=transport=dt_socket,server=y,suspend=n"]
  :main noircast.server)
