(ns htmx-example.views.contacts
  (:require
   [htmx-example.views.utils :refer [html]]))

(defn- search-form
  [search]
  [:form
   {:hx-get "/contacts-table"
    :hx-target ".contacts"
    :hx-swap "innerHTML"
    :class "tool-bar"}
   [:div {:class "mb-3"}
    [:label {:for "search" :class "form-label"} "Search Term"]
    [:input
     {:id "search",
      :class "form-control"
      :type "search",
      :name "search",
      :value search}]]])
   ; [:button {:type "submit" :class "btn btn-outline-primary"} "Search"]])

(defn contact-row [{:keys [id first last phone email]}]
  (let [row-id (format "row-%s" id)
        resource-uri (format "/contacts/%s" id)
        resource-edit-uri (format "/contacts/%s/edit" id)
        get-resource-attrs {:hx-get resource-uri
                            :hx-target "body"
                            :hx-swap "innerHTML"
                            :hx-push-url resource-uri}
        delete-confirm-msg (format "Do you really want to delete %s %s as a contact?" first last)]
    [:tr
     {:id row-id
      :class "transition-all-1s"}
     [:td [:a get-resource-attrs id]]
     [:td first]
     [:td last]
     [:td phone]
     [:td email]
     [:td
      [:button
       {:class "btn btn-outline-primary btn-sm"
        :hx-get resource-edit-uri
        :hx-target "closest tr"
        :hx-swap "outerHTML"}
       "Edit"]]
     [:td
      [:button (assoc get-resource-attrs :class "btn btn-outline-info btn-sm") "View"]]
     [:td
      [:button
       {:class "btn btn-outline-danger btn-sm"
        :hx-delete resource-uri
        :hx-swap "delete swap:1s"
        :hx-target "closest tr"
        :hx-confirm delete-confirm-msg}
       "Delete"]]]))

(defn contact-row-edit
  [{:keys [id first last phone email]}]
  (let [row-id (format "row-%s" id)
        row-id-sel (str "#" row-id)
        resource-uri (format "/contacts/%s" id)
        delete-confirm-msg (format "Do you really want to delete %s %s as a contact?" first last)]
    [:tr
     {:id row-id}
     [:form
      {:hx-put resource-uri
       :hx-target row-id-sel
       :hx-swap "outerHTML"}
      [:td [:div id]]
      [:td
       [:input {:name "first" :class "form-control" :value first}]]
      [:td
       [:input {:name "last" :class "form-control" :value last}]]
      [:td
       [:input {:name "phone" :class "form-control" :value phone}]]
      [:td
       [:input {:name "email" :class "form-control" :value email}]]
      [:td
       [:button
        {:type "submit"
         :class "btn btn-success btn-sm"}
        "Edit"]]
      [:td
       [:button {:disabled true :class "btn btn-outline-info btn-sm"} "View"]]
      [:td
       [:button
        {:class "btn btn-outline-danger btn-sm"
         :hx-delete resource-uri
         :hx-swap "delete"
         :hx-target row-id-sel
         :hx-confirm delete-confirm-msg}
        "Delete"]]]]))

(defn contacts-table [contacts]
  [:table {:class "table table-hover table-striped"}
   [:thead
    [:tr [:th "ID"] [:th "First"] [:th "Last"] [:th "Phone"] [:th "Email"] [:th] [:th] [:th]]]
   (vec (concat [:tbody] (map contact-row contacts)))])

(defn contacts-page [contacts search]
  (html [:div {:class "container"}
         [:h1 "Contacts"]
         [:h2 "A Demo Contacts Application"]
         [:div {:class "search"} (search-form search)]
         [:div {:class "contacts"} (contacts-table contacts)]
         [:div
          [:a {:class "btn btn-outline-primary" :href "/contact"} "Create Contact"]]]))

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
     [:button {:type "submit" :class "btn btn-outline-primary"} "Save"]]]
   [:p [:a {:href "/contacts"} "Back"]]])

(defn contacts-new-page
  []
  (html
   [:div {:class "container"}
    [:h1 "New Contact"]
    [:div (contact-form "/contacts")]]))

(defn contact-page
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
