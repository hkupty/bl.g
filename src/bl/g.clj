(ns bl.g
  (:require [aleph.http :as http]
            [bl.g.css :as css]
            [bl.g.html :as html]
            [reitit.ring :as ring]
            [muuntaja.core :as m]
            [reitit.http.interceptors.muuntaja :as muuntaja])
  (:gen-class))

(def base-page (html/template))

(defn base-css [_]
  {:status 200
   :body (css/base-css)
   :headers (css/base-css-headers)})

(defn content [request]
  {:status 200
   :body ((html/template) (html/parse-request request))})

(def router
  (ring/router
    [[css/*base-css-route* {:get {:handler base-css}}]
     ["/pages/{*page}" {:get {:handler content}}]]))

(defn -main
  [& args]
  (println "Serving")
  (http/start-server (ring/ring-handler
                       router
                       {:data {:muuntaja m/instance}})
                     {:port 8081}))
