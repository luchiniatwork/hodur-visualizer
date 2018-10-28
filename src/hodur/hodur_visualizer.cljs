(ns ^:figwheel-hooks hodur.hodur-visualizer
  (:require [goog.dom :as gdom]
            [hodur-engine.core :as engine]
            [hodur.schema :as schema]))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!"}))

(defn get-app-element []
  (gdom/getElement "app"))

(def conn (engine/init-schema '[Person [first-name last-name]
                                Company [^A aa ^B bb c]
                                A [^Person p]
                                B [^Company c]]))

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
