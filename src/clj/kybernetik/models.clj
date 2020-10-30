(ns kybernetik.models
  (:require [clojure.spec.alpha :as s]
            [kybernetik.models.users]))

(s/def :request/path-params (s/keys :req-un [::id]))
