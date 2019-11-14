(ns bl.g
  (:require [aleph.http :as http]
            [garden.core :as css]
            [garden.color :as clr]
            [hiccup.core :as hiccup]
            [hiccup.page :as page]
            [reitit.ring :as ring]
            [muuntaja.core :as m]
            [reitit.http.interceptors.muuntaja :as muuntaja])
  (:gen-class))

(defn random-between [from to]
  (let [r (rand)]
    (+ (* from r)
       (* to (- 1 r)))))


(defn base-css [& _]
  (let [gray-tone (random-between 0 30)]
    {:status 200
     :body (css/css [:body {:background-color (clr/rgb gray-tone
                                                       gray-tone
                                                       gray-tone)
                            :color (clr/rgb 250 250 250)
                            :font-size "14pt"
                            :font-family "Quicksand"}]
                    [:h1 {:color (clr/rgb (random-between 50 255)
                                          (random-between 70 255)
                                          (random-between 90 255))
                          :font-size "15pt"}])}))
(defn template-html [& body]
  (hiccup/html
    (page/html5 {}
                (page/include-css "/css/base.css")
                (page/include-css "https://fonts.googleapis.com/css?family=Quicksand&display=swap")
                (into [:body]
                      body)))
  )


(defn hello-world [& _]
  {:status 200
   :body (template-html [:h1 "[hkupty]"]
                        [:p "Some random stuff"])})

(def router
  (ring/router
    [["/css"
      ["/base.css"
       {:get {:handler base-css}}]]
     ["/hello-world" {:get {:handler hello-world}}]]))

(defn -main
  [& args]
  (println "Serving")
  (http/start-server (ring/ring-handler
                       router
                       {:data {:muuntaja m/instance}})
                     {:port 8081}))
