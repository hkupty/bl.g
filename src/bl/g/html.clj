(ns bl.g.html
  (:require [hiccup.core :as hiccup]
            [hiccup.page :as page]
            [clojure.string :as str]
            [hkupty.toolkit :refer [tap label]]
            [commonmark-hiccup.core :as md-html]
            [aero.core :as aero]
            [clojure.walk :refer [postwalk]]
            [commonmark-hiccup.core :refer [markdown->html]]
            [clojure.java.io :as io]))

(def ^:dynamic *base-template* "template.edn")

(defmethod aero/reader 'html/include-css [_ tag value]
  (page/include-css value))

(defmethod aero/reader 'html/placeholder [_ tag value]
  (with-meta [] {:placeholder value}))


(defn include-value [kv-coll form]
  (postwalk
    (fn [i]
      (let [{:keys [placeholder]} (meta i)]
        (cond-> i
          (some? placeholder) (into (get-in kv-coll placeholder)))))
    form))

(defn read-file [fname]
  (io/file (io/resource fname)))

(defn read-page [resource]
  (tap resource)
  (or
    (some-> (read-file (str resource ".edn"))
            (aero/read-config))
    (some-> (read-file (str resource ".md"))
            (md-html/markdown->hiccup md-html/default-config))))

(defn template
  ([fname]
   (let [templ (aero/read-config (read-file fname))]
     (fn [kv-data]
       (hiccup/html
         (include-value kv-data
                        (into [:html {:encoding "UTF-8"}]
                              templ))))))
  ([] (template *base-template*)))

(defn parse-request [request]
  (let [route (:page (:path-params request))]
    {:content (read-page route)
     :title (last (str/split route #"/"))}))
