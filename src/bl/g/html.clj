(ns bl.g.html
  (:require [hiccup.core :as hiccup]
            [hiccup.page :as page]
            [clojure.string :as str]
            [commonmark-hiccup.core :as md-html]
            [aero.core :as aero]
            [clojure.walk :refer [postwalk]]
            [commonmark-hiccup.core :refer [markdown->html]]
            [clojure.java.io :as io]))

(defn tap [x] (println x) x)

(def ^:dynamic *base-template* "template.edn")

(defmulti fill-placeholder (fn [[key- _] _] key-))

(defmethod fill-placeholder ::menu [[_ container] maybe-val]
  [container (or maybe-val
                 [:a {:href "/"} "home"])])

(defmethod fill-placeholder :default [[_ container] maybe-val]
  (cond-> [container]
    (some? maybe-val) (conj maybe-val)))

(defmethod fill-placeholder ::content [[_ container] maybe-val]
  (into [container] maybe-val))

(defmethod fill-placeholder ::banner [[_ container] maybe-val]
  [container (or maybe-val
                 ["img" {:src "https://images.unsplash.com/photo-1584592017506-78c9e5ee5b11?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1490&q=80"}])])

(defmethod fill-placeholder ::footer [[_ container] maybe-val]
  [container (or maybe-val
                 [:div.column [:p "asdf"]])])

(defmethod fill-placeholder ::title [[key- container] maybe-val]
  [container (or maybe-val
                 "le-title")])

(defmethod aero/reader 'html/include-css [_ tag value]
  (page/include-css value))

(defmethod aero/reader 'html/placeholder [_ tag name-and-container]
  (with-meta name-and-container {:placeholder? true}))

(defn include-value [kv-coll form]
  (postwalk
         (fn [i]
           (let [{:keys [placeholder?]} (meta i)]
             (cond-> i
                    placeholder? (fill-placeholder (get kv-coll (first i))))))
         form))

(defn read-file [fname]
  (io/file (io/resource fname)))

(defn read-page [resource]
  (or
    (some-> (str resource ".edn")
             (read-file)
             (aero/read-config))
    (some->> (str resource ".md")
             (read-file)
             (slurp)
             (md-html/markdown->hiccup md-html/default-config)
             (assoc {} ::content))))

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
    (read-page route)))
