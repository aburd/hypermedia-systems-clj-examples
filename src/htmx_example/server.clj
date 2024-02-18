(ns htmx-example.server
  (:require
   [muuntaja.core :as m]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as rrc]
   [reitit.coercion.malli :as malli]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.adapter.jetty :as jetty]
   [hiccup2.core :as h]
   [faker.name :as name]
   [faker.phone-number :as phone]
   [faker.internet :as internet]))

(defonce ^:private s (atom nil))

(defn redirect [url]
  {:status 301
   :headers {"Location" url}})

(defn success [body & {:keys [mime-type]
                       :or {mime-type "text/html"}}]
  {:status 200
   :body body
   :headers {"Content-Type" mime-type}})

(defn html [body]
  (h/html [:html
           [:head
            [:meta {:charset "utf-8"}]
            [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
            [:link {:rel "stylesheet"
                    :href "https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"}]]
           [:body body]]))

(defn generate-contact []
  {:id (rand-int 1000) :first (name/first-name) :last (name/last-name) :phone (first (phone/phone-numbers)) :email (internet/email)})

(def contacts
  (take
   10
   (repeatedly
    #(generate-contact))))

(defn search-contacts
  [search]
  (vec (if (or (nil? search) (empty? search))
         contacts
         (filter #(or (= search (:first %)) (= search (:last %))) contacts))))

(defn search-form
  [search]
  [:form
   {:action "/contacts", :method "get", :class "tool-bar"}
   [:label {:for "search"} "Search Term"]
   [:input
    {:id "search",
     :type "search",
     :name "search",
     :value search}]
   [:input {:type "submit", :value "Search"}]])

(defn contact-row [{:keys [id first last phone email]}]
  [:tr
   [:td id]
   [:td first]
   [:td last]
   [:td phone]
   [:td email
    [:a {:href (str "/contacts/" id "/edit")} "Edit"]
    [:a {:href (str "/contacts/" id)} "View"]]])

(defn contacts-table [contacts]
  [:table
   [:thead
    [:tr [:td "ID"] [:th "First"] [:th "Last"] [:th "Phone"] [:th "Email"]]]
   (concat [:tbody] (map contact-row contacts))])

(defn contacts-handler
  [{{{:keys [search]} :query} :parameters}]
  (let [contacts (search-contacts search)]
    (success
     (str
      (html [:div {:class "container"}
             [:div {:class "search"} (search-form search)]
             [:div {:class "contacts"} (contacts-table contacts)]])))))

(def app
  (ring/ring-handler
   (ring/router
    [["/" {:get {:handler (fn [_req] (redirect "/contacts"))}}]
     ["/contacts" {:parameters {:query [:map [:search {:optional true} string?]]}
                   :get {:handler contacts-handler}}]
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
