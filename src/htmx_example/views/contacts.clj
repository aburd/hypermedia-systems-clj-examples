(ns htmx-example.views.contacts
  (:require
   [htmx-example.views.utils :refer [html]]))

(defn- search-form
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

(defn- contact-row [{:keys [id first last phone email]}]
  [:tr
   [:td id]
   [:td first]
   [:td last]
   [:td phone]
   [:td email
    [:a {:href (str "/contacts/" id "/edit")} "Edit"]
    [:a {:href (str "/contacts/" id)} "View"]]])

(defn- contacts-table [contacts]
  [:table
   [:thead
    [:tr [:td "ID"] [:th "First"] [:th "Last"] [:th "Phone"] [:th "Email"]]]
   (concat [:tbody] (map contact-row contacts))])

(defn contacts-page [contacts search]
  (html [:div {:class "container"}
         [:div {:class "search"} (search-form search)]
         [:div {:class "contacts"} (contacts-table contacts)]]))
