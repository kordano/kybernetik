(ns kybernetik.routes.services
  (:require
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring.coercion :as coercion]
   [reitit.coercion.spec :as spec-coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.parameters :as parameters]
   [kybernetik.middleware :as middleware]
   [kybernetik.middleware.formats :as formats]
   [kybernetik.middleware.exception :as exception]
   [ring.util.http-response :refer :all]
   [kybernetik.models]
   [kybernetik.controllers
    [users :as kcu]]
   [clojure.java.io :as io]))

(defn service-routes []
  ["/api"
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 exception/exception-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware

                 middleware/wrap-auth
                 ]}

   ;; swagger documentation
   ["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "Kybernetik Public API"
                            :description "Cybernetics"}
                     :securityDefinitions {:JWT {:type "apiKey"
                                                 :name "Authorization"
                                                 :in "header"}}
                     :security [{:JWT []}]}
           :handler (swagger/create-swagger-handler)}}]

   ["/api-docs/*"
    {:get (swagger-ui/create-swagger-ui-handler
           {:url "/api/swagger.json"
            :config {:validator-url nil}})}]

   ["/login"
    {:post {:responses {200 {:body :kybernetik.models.users/jws}}
            :parameters {:body :kybernetik.models.users/credential}
            :swagger {:tags ["self-service"]}
            :handler kcu/sign-in}}]

   ["/users" {:middleware [middleware/wrap-restricted]}
    ["" {:post {:parameters {:body :kybernetik.models.users/new-user}
             :handler kcu/create}
          :get {:responses {200 {:body :kybernetik.models.users/user-list}}
            :handler kcu/all}}]]])
