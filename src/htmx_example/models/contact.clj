(ns htmx-example.models.contact
  (:require
   [clojure.string :as s]
   [faker.name :as name]
   [faker.phone-number :as phone]
   [faker.internet :as internet]))

(defn generate-contact []
  {:id (rand-int 1000) :first (name/first-name) :last (name/last-name) :phone (first (phone/phone-numbers)) :email (internet/email)})

(defonce contacts
  (atom (vec (take
              10
              (repeatedly
               #(generate-contact))))))

; (reset! contacts (vec (take
;                        10
;                        (repeatedly
;                         #(generate-contact)))))

(defn search
  [search]
  (vec (if (or (nil? search) (empty? search))
         @contacts
         (filter #(or (s/includes? (:first %) search) (s/includes? (:last %) search)) @contacts))))

(defn add [{:keys [first last email phone]}]
  (swap! contacts conj {:id (rand-int 1000)
                        :first first
                        :last last
                        :phone phone
                        :email email}))

(defn update [{:keys [id first last email phone]}]
  (let [new-v (reduce
               (fn [coll c]
                 (conj coll (if (= id (:id c))
                              {:id id :first first :last last :email email :phone phone}
                              c)))
               []
               @contacts)]
    (reset! contacts new-v)))

(defn get [id]
  (first (filter #(= id (:id %)) @contacts)))

(defn delete [id]
  (reset! contacts (vec (remove #(= id (:id %)) @contacts))))
