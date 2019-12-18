(ns bl.g.css
  (:require [aero.core :as aero]
            [garden.core :as css]
            [garden.color :as clr]
            [clojure.java.io :as io]))

(def ^:dynamic *base-css-file* "css.edn")
(def ^:dynamic *base-css-route* "/css/base.css")
(def ^:dynamic *base-css-cache-duration* 900)

(defn random-between [from to]
  (let [r (rand)]
    (+ (* from r)
       (* to (- 1 r)))))

(defmethod aero.core/reader 'css/random [_ tag value]
  (apply random-between value))

(defmethod aero.core/reader 'css/rgb [_ tag value]
  (apply clr/rgb value))

(defn resource->css [fname]
  (->> fname
      (io/resource)
      (aero/read-config)
      :css
      (apply css/css)))

(defn base-css-headers
  ([] {"Cache-Control" (str "private, max-age=" *base-css-cache-duration*)})
  ([overrides] (merge (base-css-headers)
                      overrides)))

(defn base-css []
  (resource->css *base-css-file*))
