(ns htmx-example.views.archives
  (:require
   [htmx-example.views.utils :refer [html]]
   [htmx-example.models.archive :as archive]))

(defn create-archive-form [action & {:keys [archive-id dir-path]
                                     :or {archive-id "" dir-path ""}}]
  [:div
   [:form
    {:action action
     :method "post"
     :hx-boost "true"
     :hx-swap "outerHTML"
     :hx-target "this"}
    [:fieldset
     [:legend "Archive Fields"]
     [:div
      {:class "mb-3"}
      [:label {:for "archive-archive-id"
               :class "form-label"}
       "Archive archive-id"]
      [:input
       {:id "archive_id",
        :class "form-control"
        :name "archive_id",
        :type "text",
        :required true
        :placeholder "music",
        :value archive-id}]
      [:span {:class "error"} ""]]
     [:div
      {:class "mb-3"}
      [:label {:for "dir_path"
               :class "form-label"}
       "Path"]
      [:input
       {:archive-id "dir_path"
        :class "form-control"
        :id "dir_path"
        :name "dir_path"
        :type "text",
        :required true
        :placeholder "/home/aburd/Music",
        :value dir-path}]
      [:span {:class "error"} ""]]
     [:button {:type "submit" :class "btn btn-outline-primary"} "Create Archive"]]]
   [:p [:a {:href "/archives"} "Back"]]])

(defn archive-item
  [archive-id]
  [:li
   [:a {:href (format "/archives/%s" archive-id)
        :hx-boost "true"
        :hx-push-url true}
    archive-id]])

(defn- archives-list []
  [:div {:class "archives-list"}
   [:ul (map #(archive-item %) (archive/get-archive-files))]])

(defn archive-progress
  [archive-id]
  (let [percent (archive/progress-percent archive-id)]
    [:div {:class "progress"}
     [:div {:id "archive-progress"
            :class (str "progress-bar progress-bar-striped progress-bar-animated" (if (>= 100.0 percent) " bg-success" ""))
            :role "progressbar"
            :aria-valuenow percent
            :aria-valuemin "0"
            :aria-valuemax "100"
            :style (format "width:%s" (str (int percent) "%"))}]]))

(defn archive-status
  [archive-id]
  (let [status (archive/get-status archive-id)
        text (case status
               :waiting "Waiting to start"
               :running "Archive being created..."
               :done "Archive has been created.")]
    (if (= :done status)
      [:div
       {:hx-get "/archives"
        :hx-boost "true"
        :hx-trigger "load delay:1s"
        :hx-target "body"}
       [:div {:class "message"} text]
       (archive-progress archive-id)]
      [:div
       {:hx-get (format "/archives/%s/status" archive-id)
        :hx-trigger "every 3s"
        :hx-target "this"
        :hx-swap "outerHTML"}
       [:div {:class "message"} text]
       (archive-progress archive-id)])))

(defn archives-page
  []
  (html
   [:div {:class "container"}
    [:h1 "Archives"]
    [:div (archives-list)]
    [:h2 "Create Archive"]
    [:div (create-archive-form "/archives")]]))

(defn archive-page
  [archive-id]
  (html
   [:div {:class "container"}
    [:h1 (format "Archive: %s" archive-id)]
    [:div
     [:h3 "Download"]
     [:div [:a {:class "button"
                :href (format "/archives/%s/download" archive-id)} "Download archive"]]
     [:h3 "Output"]
     [:pre (archive/get-output archive-id)]]]))

