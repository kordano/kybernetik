(ns kybernetik.controllers.users
  (:require [kybernetik.db.core :as db]
            [kybernetik.responses :as r]))

(defn create [{{{:keys [name firstname email lastname password role]} :body} :parameters :as request}]
  (let [new-user {:user/name name
                  :user/email email
                  :user/firstname firstname
                  :user/lastname lastname
                  :user/password password
                  :user/role (keyword "role" role)}]
    (try
      (r/ok {:id (db/create-user new-user)})
      (catch Exception e
        (r/bad-request {:message (.getMessage e)})))) )
