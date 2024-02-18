(ns htmx-example.views.contacts
  (:require
   [htmx-example.views.utils :refer [html]]))

(defn- search-form
  [search]
  [:form
   {:action "/contacts", :method "get", :class "tool-bar"}
   [:div {:class "mb-3"}
    [:label {:for "search" :class "form-label"} "Search Term"]
    [:input
     {:id "search",
      :class "form-control"
      :type "search",
      :name "search",
      :value search}]]
   [:button {:type "submit" :class "btn btn-primary"} "Search"]])

(defn- contact-row [{:keys [id first last phone email]}]
  [:tr
   [:td id]
   [:td first]
   [:td last]
   [:td phone]
   [:td email]
   [:td
    [:a {:href (str "/contacts/" id "/edit")} "Edit"]]
   [:td
    [:a {:href (str "/contacts/" id)} "View"]]])

(defn- contacts-table [contacts]
  [:table {:class "table"}
   [:thead
    [:tr [:td "ID"] [:th "First"] [:th "Last"] [:th "Phone"] [:th "Email"] [:th] [:th]]]
   (vec (concat [:tbody] (map contact-row contacts)))])

(defn contacts-page [contacts search]
  (html [:div {:class "container"}
         [:h1 "Contacts"]
         [:h2 "A Demo Contacts Application"]
         [:div {:class "search"} (search-form search)]
         [:div {:class "contacts"} (contacts-table contacts)]]))
