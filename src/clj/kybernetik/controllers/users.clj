(ns kybernetik.controllers.users
  (:require [kybernetik.db.core :as db]
            [kybernetik.utils :as u]
            [kybernetik.responses :as r]))

(defn create [{{{:keys [name firstname email lastname password role]} :body} :parameters}]
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

(defn all [req]
  (r/ok {:users (mapv (fn [user]
                        (-> user
                            (update :user/role (comp name :db/ident))
                            (update :user/supervisor u/remove-namespace)
                            u/remove-namespace))
                      (db/list-users))}))

