(ns kybernetik.models.users
  (:require [clojure.spec.alpha :as s]))

(s/def :new-user/name string?)
(s/def :new-user/email string?)
(s/def :new-user/firstname string?)
(s/def :new-user/password string?)
(s/def :new-user/lastname string?)
(s/def :new-user/role #{"contractor" "employee" "manager" "admin"})

(s/def ::new-user (s/keys :req-un [:new-user/name
                                   :new-user/email
                                   :new-user/firstname
                                   :new-user/lastname
                                   :new-user/password
                                   :new-user/role]))

(s/def :ref-user/id number?)
(s/def :ref-user/firstname string?)
(s/def :ref-user/lastname string?)
(s/def :ref-user/ref string?)
(s/def ::ref-user (s/keys :req-un [:ref-user/id
                                   :ref-user/firstname
                                   :ref-user/lastname
                                   :ref-user/ref]))


(s/def :list-user/id number?)
(s/def :list-user/name string?)
(s/def :list-user/email string?)
(s/def :list-user/firstname string?)
(s/def :list-user/lastname string?)
(s/def :list-user/ref string?)
(s/def :list-user/role #{"contractor" "employee" "manager" "admin"})
(s/def :list-user/supervisor ::ref-user)
(s/def ::list-user (s/keys :req-un [:list-user/id
                                    :list-user/name
                                    :list-user/email
                                    :list-user/firstname
                                    :list-user/lastname
                                    :list-user/ref
                                    :list-user/role]
                           :opt-un [:list-user/supervisor]))
(s/def :list-user/users (s/coll-of ::list-user))

(s/def ::user-list (s/keys :req-un [:list-user/users]))

(s/def :credential/email string?)
(s/def :credential/password string?)
(s/def ::credential (s/keys :req-un [:credential/email
                                     :credential/password]))

(s/def :jws/token string?)
(s/def ::jws (s/keys :req-un [:jws/token]))
