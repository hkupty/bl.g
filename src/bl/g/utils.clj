(ns bl.g.utils
  (:import (java.security MessageDigest)
            (java.util Base64)))

(defn sha-256 [^bytes v]
  (->> v
       (.digest (MessageDigest/getInstance "SHA-256"))
       (.encodeToString (Base64/getEncoder))))
