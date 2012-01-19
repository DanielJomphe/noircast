(defproject noircast "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [noir "1.2.2"]
                 [clj-stacktrace "0.2.4"] ;fixes a transitive vn conflict
                 ]
  :jvm-opts ["-agentlib:jdwp=transport=dt_socket,server=y,suspend=n"]
  :main noircast.server)
