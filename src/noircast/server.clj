(ns noircast.server
  (:require [noir.server :as server]))

(server/load-views "src/noircast/views/")

(declare get-server-port)
(declare get-noir-params)

(defn -main [& m]
  (server/start (get-server-port)
                (get-noir-params m)))

(comment ; for running through the REPL instead of `lein run`...
  (def server (-main))
  (def server (-main "dev"  "simple"))  ; defaults
  (def server (-main "dev"  "advanced"))
  (def server (-main "prod" "simple"))
  (def server (-main "prod" "advanced"))
  (server/stop server)
  )

(defn env
  "Gets the value of a definition in the System environment.
   If none is set, returns the default passed in."
  [name default]
  (get (System/getenv) name default))

(defn get-server-port
  "Gets the port number for the server from the System environment.
   For now, this means:
     * taken from the System environment's PORT definition,
               or the default 8080 in last resort.
   Returns it as an Integer."
  []
  (Integer. (env "PORT" "8080")))

(defn get-noir-params
  "Gets the Noir parameters from the optional argument values.
   For now, this means:
     * :ns as the hardcoded project's base namespace. TODO make configurable?
     * :mode taken from the first optional argument value,
                     or the System environment's NOIR_MODE definition,
                     or the default :dev in last resort.
     * :cljsc {:optimizations ??} taken from the second optional arg's value,
                     or the System environment's CLJSC_OPTI definition,
                     or the default :simple in last resort.
   Returns everything mapped as expected by Noir."
  [& vs]
  (let [mode (keyword (or (first  vs) (env "NOIR_MODE" :dev)))
        opti (keyword (or (second vs) (env "CLJSC_OPTI" :simple)))]
    {:ns    'noircast
     :mode  mode
     :cljsc {:optimizations opti}}))

