(ns htmx-example.models.contact
  (:require
   [clojure.string :as s]
   [faker.name :as name]
   [faker.phone-number :as phone]
   [faker.internet :as internet]))

(defn generate-contact []
  {:id (rand-int 1000) :first (name/first-name) :last (name/last-name) :phone (first (phone/phone-numbers)) :email (internet/email)})

(def contacts
  (take
   100
   (repeatedly
    #(generate-contact))))

(defn search
  [search]
  (vec (if (or (nil? search) (empty? search))
         contacts
         (filter #(or (s/includes? (:first %) search) (s/includes? (:last %) search)) contacts))))
