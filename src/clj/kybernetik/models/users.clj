(ns kybernetik.models.users
  (:require [clojure.spec.alpha :as s]))

(s/def :new-user/name string?)
(s/def :new-user/email string?)
(s/def :new-user/firstname string?)
(s/def :new-user/password string?)
(s/def :new-user/lastname string?)
(s/def :new-user/role string?)

(s/def ::new-user (s/keys :req-un [:new-user/name
                                   :new-user/email
                                   :new-user/firstname
                                   :new-user/lastname
                                   :new-user/password
                                   :new-user/role]))
