(ns ^:figwheel-hooks hodur.hodur-visualizer
  (:require [goog.dom :as gdom]
            [hodur-engine.core :as engine]
            [hodur.schema :as schema]))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!"}))

(defn get-app-element []
  (gdom/getElement "app"))

(def conn (engine/init-schema
           '[^{:graphviz/tag true}
             default
             
             A
             [^{:optional true} f1
              ^{:type String
                :optional true} f2
              f3 [af1
                  ^C af2
                  ^{:type B
                    :cardinality n
                    :optional true}
                  af3]]

             ^{:graphviz/color "navajowhite1"
               :ble/tag-recursive true}
             B
             [^A f1]

             ^{:implements LaInterface}
             C
             []

             ^{:interface true}
             LaInterface
             [field-in-interface]

             ^{:union true
               :graphviz/group "group1"}
             Lunion
             [D E]

             ^{:graphviz/group "group1"}
             D
             []

             ^{:bla/tag true}
             E
             [^Lenum lenum
              ^{:type B
                :cardinality [0 n]
                :optional true} le-b]

             ^{:enum true}
             Lenum
             [is-this
              is-that]]))

(defn ^:export set-model []
  (let [{:keys [nodes links]} (schema/schema conn)]
    (set! (.-model js/myDiagram) (js/go.GraphLinksModel. nodes links))))

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (set-model)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
