(ns htmx-example.response)

(defn redirect [url]
  {:status 301
   :headers {"Location" url}})

(defn success [body & {:keys [mime-type]
                       :or {mime-type "text/html"}}]
  {:status 200
   :body body
   :headers {"Content-Type" mime-type}})

(defn not-found []
  {:status 404
   :body "<h1>Not found</h1>"})
