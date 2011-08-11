(ns noircast.server
  (:require [noir.server :as server]
            [noir.util.cljs :as cljs]))

(server/add-middleware cljs/wrap-cljs)

(server/load-views "src/noircast/views/")

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        cljsc-mode (keyword (or (second m) :simple))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'noircast
                        :cljsc {:optimizations cljsc-mode}})))

(comment ; for running through the REPL instead of `lein run`...
  (def server (-main))
  (def server (-main "dev"  "simple"))
  (def server (-main "dev"  "advanced"))
  (def server (-main "prod" "simple"))
  (def server (-main "prod" "advanced"))
  (server/stop server)
  )
