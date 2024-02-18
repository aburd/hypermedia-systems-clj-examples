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
   [:td [:a {:href (format "/contacts/%s/show" id)} id]]
   [:td first]
   [:td last]
   [:td phone]
   [:td email]
   [:td
    [:a {:href (format "/contacts/%s/edit" id)} "Edit"]]
   [:td
    [:a {:href (format "/contacts/%s/show" id)} "View"]]])

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

(defn- contact-form [action & {:keys [email first last phone]
                               :or {email "" first "" last "" phone ""}}]
  [:div
   [:form
    {:action action :method "post"}
    [:fieldset
     [:legend "Contact Values"]
     [:div
      {:class "mb-3"}
      [:label {:for "email"
               :class "form-label"}
       "Email"]
      [:input
       {:name "email",
        :class "form-control"
        :id "email",
        :type "email",
        :required true
        :placeholder "Email",
        :value email}]
      [:span {:class "error"} ""]]
     [:div
      {:class "mb-3"}
      [:label {:for "first_name"
               :class "form-label"}
       "First Name"]
      [:input
       {:name "first_name",
        :class "form-control"
        :id "first_name",
        :type "text",
        :required true
        :placeholder "First Name",
        :value first}]
      [:span {:class "error"} ""]]
     [:div
      {:class "mb-3"}
      [:label {:for "last_name" :class "form-label"} "Last Name"]
      [:input
       {:name "last_name",
        :class "form-control"
        :id "last_name",
        :type "text",
        :required true
        :placeholder "Last Name",
        :value last}]
      [:span {:class "error"} ""]]
     [:div
      {:class "mb-3"}
      [:label {:for "phone" :class "form-label"} "Phone"]
      [:input
       {:name "phone",
        :class "form-control"
        :id "phone",
        :type "text",
        :required true
        :placeholder "Phone",
        :value phone}]
      [:span {:class "error"} ""]]
     [:button {:type "submit" :class "btn btn-primary"} "Save"]]]
   [:p [:a {:href "/contacts"} "Back"]]])

(defn contacts-new-page
  []
  (html
   [:div {:class "container"}
    [:h1 "New Contact"]
    [:div (contact-form "/contacts/new")]]))

(defn contacts-edit-page
  [contact]
  (html
   [:div {:class "container"}
    [:h1 "Edit Contact 1"]
    [:div (contact-form
           (format "/contacts/%s/edit" (:id contact))
           contact)]]))

(defn contacts-show-page
  [{:keys [id phone email last first] :as _contact}]
  (html
   [:div {:class "container"}
    [:h1 (format "%s %s" first last)]
    [:div
     [:div (format "Phone: %s" phone)]
     [:div (format "Email: %s" email)]]
    [:p
     [:a {:href (format "/contacts/%s/edit", id)} "Edit"]
     [:a {:href "/contacts"} "Back"]]]))
