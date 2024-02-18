(ns htmx-example.server
  (:require
   [muuntaja.core :as m]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as rrc]
   [reitit.coercion.malli :as malli]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.adapter.jetty :as jetty]
   [htmx-example.controllers.contacts :as contacts]
   [htmx-example.response :refer [redirect]]))

(defonce ^:private s (atom nil))

(def app
  (ring/ring-handler
   (ring/router
    [["/" {:get {:handler (fn [_req] (redirect "/contacts"))}}]
     ["/contacts" {:parameters {:query [:map [:search {:optional true} string?]]}
                   :get {:handler contacts/get-contacts-handler}}]
     ["/api"
      ["/math" {:get {:parameters {:query {:x int?, :y int?}}
                      :responses  {200 {:body {:total int?}}}
                      :handler    (fn [{{{:keys [x y]} :query} :parameters}]
                                    {:status 200
                                     :body   {:total (+ x y)}})}}]]]
      ;; router data affecting all routes
    {:data {:coercion   malli/coercion
            :muuntaja   m/instance
            :middleware [parameters/parameters-middleware
                         rrc/coerce-request-middleware
                         muuntaja/format-response-middleware
                         rrc/coerce-response-middleware]}})))

(defn start []
  (when (nil? @s)
    (reset! s (jetty/run-jetty app {:port 3000
                                    :join? false}))))

(defn stop []
  (when (not (nil? @s))
    (.stop @s)
    (reset! s nil)))

(comment
  (stop)
  (start))
