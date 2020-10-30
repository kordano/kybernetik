(ns kybernetik.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.dom :as rdom]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [markdown.core :refer [md->html]]
    [kybernetik.ajax :as ajax]
    [kybernetik.events]
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe]
    [clojure.string :as string])
  (:import goog.History))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page @(rf/subscribe [:common/page])) :is-active)}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
              [:nav.navbar.is-light>div.container
               [:div.navbar-brand
                [:a.navbar-item {:href "/" :style {:font-weight :bold}} "kybernetik"]
                [:span.navbar-burger.burger
                 {:data-target :nav-menu
                  :on-click #(swap! expanded? not)
                  :class (when @expanded? :is-active)}
                 [:span][:span][:span]]]
               [:div#nav-menu.navbar-menu
                {:class (when @expanded? :is-active)}
                [:div.navbar-start
                 [nav-link "#/" "Home" :home]
                 [nav-link "#/users" "Users" :user-list]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(defn home-page []
  [:section.section>div.container>div.content>h2 "Welcome"])

(defn users-list-page []
  [:section.section>div.container>div.content>div.card
   [:header.card-header
    [:p.card-header-title "User List"]
    ]
   [:div.card-content
    [:div.content
     (when-let [users @(rf/subscribe [:users])]
       (if-not (empty? users)
         [:table.table
          [:thead [:tr
                   [:th "ID"]
                   [:th "ref"]
                   [:th "firstname"]
                   [:th "lastname"]
                   [:th "role"]
                   [:td ""]]]
          [:tbody (for [{:keys [id firstname lastname ref role]} users]
                    [:tr
                     [:td id]
                     [:td ref]
                     [:td firstname]
                     [:td lastname]
                     [:td role]
                     [:td [:div.buttons [:a {:href "/"}
                                         [:span.icon.is-medium.has-text-warning>i.mdi.mdi-24px.mdi-square-edit-outline]]] ]])]]
         [:p.has-text-grey.center (str "No results found.")]))]
    [:footer.card-footer]]])

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div
     [navbar]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
    [["/" {:name        :home
           :view        #'home-page}]
     ["/users" {:name :users
                :view #'users-list-page
                :controllers [{:start (fn [_] (rf/dispatch [:page/init-users]))}]}]]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))

(comment

  (init!)

  )
