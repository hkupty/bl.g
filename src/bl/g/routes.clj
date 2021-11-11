(ns bl.g.routes
  (:require [clojure.java.io :as io])
  )

(def page-store (atom {}))

(defn load-page! [page]
  (println (.getName page)))

(defn load-pages! []
  (run! load-page! (file-seq (io/file (io/resource "pages")))))



(comment
  (load-pages!)
  )
