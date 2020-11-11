(ns kybernetik.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]))

;;dispatchers

(rf/reg-event-db
  :common/navigate
  (fn [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                                 (rfc/apply-controllers (:controllers old-match) match))]
      (assoc db :common/route new-match))))

(rf/reg-fx
  :common/navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :common/navigate!
  (fn [_ [_ url-key params query]]
    {:common/navigate-fx! [url-key params query]}))

(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

(rf/reg-event-db
 :set-users
 (fn [db [_ {:keys [users]}]]
   (assoc db :users users)))

(rf/reg-event-fx
 :fetch-users
 (fn [{db :db} _]
   {:http-xhrio {:method :get
                 :uri "/api/users"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :headers {"Authorization" (str "Token " (:token db))}
                 :on-success [:set-users]
                 :on-failure [:set-message]}}))

(rf/reg-event-db
 :set-token
 (fn [db [_ {:keys [token]}]]
   (assoc db :token token)))

(rf/reg-event-db
 :set-message
 (fn [db [_ {{:keys [message]} :response}]]
   (js/alert message)
   db))

(rf/reg-event-fx
 :sign-in
 (fn [_ [_ credentials]]
   {:http-xhrio {:method :post
                 :uri "/api/login"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :format (ajax/json-request-format)
                 :params credentials
                 :on-success [:set-token]
                 :on-failure [:set-message]}}))


(rf/reg-event-fx
 :page/init-users
 (fn [_ _]
   {:dispatch [:fetch-users]}))

(rf/reg-event-db
 :delete-token
 (fn [db _]
   (dissoc db :token)))

(rf/reg-event-fx
 :sign-out
 (fn [_ _]
   {:dispatch [:delete-token]}))

;;subscriptions

(rf/reg-sub
  :common/route
  (fn [db _]
    (-> db :common/route)))

(rf/reg-sub
  :common/page-id
  :<- [:common/route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :common/page
  :<- [:common/route]
  (fn [route _]
    (-> route :data :view)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
 :users
 (fn [db _]
   (:users db)))

(rf/reg-sub
 :credentials/token
 (fn [db _]
   (:token db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))

