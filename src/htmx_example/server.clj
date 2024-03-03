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
   [htmx-example.controllers.archives :as archives]
   [htmx-example.response :refer [redirect]]
   [ring.logger :as logger]))

(defonce ^:private s (atom nil))

(def app
  (ring/ring-handler
   (ring/router
    [["/" {:get {:handler (fn [_req] (redirect "/contacts"))}}]
     ["/archives" {:parameters {}
                   :get {:handler archives/archives-handler}
                   :post {:parameters {:form [:map
                                              [:archive_id string?]
                                              [:dir_path string?]]}
                          :handler archives/archives-create-handler}}]
     ["/archives/:archive-id" {:parameters {:path [:map [:archive-id string?]]}
                               :get {:handler archives/archive-handler}}]
     ["/archives/:archive-id/status" {:parameters {:path [:map [:archive-id string?]]}
                                      :get {:handler archives/archive-get-status-handler}}]
     ["/archives/:archive-id/download" {:parameters {:path [:map [:archive-id string?]]}
                                        :get {:handler archives/archive-download-handler}}]
     ["/contacts" {:parameters {:query [:map [:search {:optional true} string?]]}
                   :get {:handler contacts/contacts-handler}}]
     ["/contacts-table" {:parameters {:query [:map [:search {:optional true} string?]]}
                         :get {:handler contacts/contacts-table-handler}}]
     ["/contact" {:get {:handler contacts/contacts-new-handler}
                  :post {:parameters {:form [:map
                                             [:first_name string?]
                                             [:last_name string?]
                                             [:phone string?]
                                             [:email string?]]}
                         :handler contacts/contacts-create-handler}}]
     ["/contacts/:contact-id" {:parameters {:path [:map [:contact-id int?]]}
                               :get {:handler contacts/contact-handler}
                               :delete {:handler contacts/contacts-delete-handler}
                               :put {:handler contacts/contacts-update-handler
                                     :parameters {:form [:map
                                                         [:first string?]
                                                         [:last string?]
                                                         [:phone string?]
                                                         [:email string?]]}}}]
     ["/contacts/:contact-id/edit" {:parameters {:path [:map [:contact-id int?]]}
                                    :get {:handler contacts/contacts-edit-handler}}]]
      ;; router data affecting all routes
    {:data {:coercion   malli/coercion
            :muuntaja   m/instance
            :middleware [logger/wrap-with-logger
                         parameters/parameters-middleware
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
