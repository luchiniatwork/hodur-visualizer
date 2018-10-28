(ns hodur.schema
  (:require [datascript.core :as d]))


(defn ^:private cardinality-label
  [[from to]]
  (if (= from to) from (str from ".." to)))

(defn ^:private parse-field-edge
  [{:keys [field/name field/cardinality param/_parent] :as field}]
  (let [src (-> field :field/parent)
        dst (-> field :field/type)]
    (when (and src dst)
      {:from (-> src :type/name)
       :to (-> dst :type/name)
       :text name
       :toText (cardinality-label cardinality)})
    #_(str (edge-label name src dst cardinality)
           (parse-params-edges _parent))))

(defn ^:private parse-type-edges
  [{:keys [field/_parent] :as t}]
  (reduce (fn [c field]
            (if-let [parsed-field (parse-field-edge field)]
              (conj c parsed-field)
              c))
          [] _parent))

(defn ^:private parse-edges
  [types]
  (reduce (fn [c t]
            (concat c
                    (parse-type-edges t)
                    #_(parse-implements-edges t)
                    #_(parse-union-edges t)))
          "" types))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn ^:private parse-field
  [{:keys [field/name] :as field}]
  {:name name
   :figure "Decision"
   :color "yellow"})

(defn ^:private parse-fields
  [fields]
  (reduce (fn [c f]
            (conj c (parse-field f)))
          [] fields))

(defn ^:private parse-node
  [{:keys [type/name field/_parent] :as t}]
  {:key name
   :items (parse-fields _parent)})

(defn ^:private parse-nodes
  [types]
  (reduce (fn [c t]
            (conj c (parse-node t)))
          [] types))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn ^:private meta-query []
  '[:find [(pull ?e [{:type/implements [*]}
                     {:field/_parent
                      [{:field/type [*]}
                       {:field/parent [*]}
                       {:param/_parent
                        [{:param/type [*]}
                         {:param/parent
                          [{:field/parent [*]} *]}
                         *]}
                       *]}
                     *]) ...]
    :where
    [?e :type/name]
    [?e :type/nature :user]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Public functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn schema [conn]
  (let [types (d/q (meta-query) @conn)]
    {:nodes (clj->js (parse-nodes types))
     :links (clj->js (parse-edges types))}))
