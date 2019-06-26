/*
    GoogleGaugeChart
    ========================

    @file      : GoogleGaugeChart.js
    @version   : 1.0.0
    @author    : Marcus Groen
    @date      : 14 june 2019
    @copyright : Marcus Groen
    @license   : Apache 2

    Documentation
    ========================
    A gauge with a dial, rendered within the browser using SVG or VML.
*/

define([
    'dojo/_base/declare',
    'GoogleCharts/widget/base',
    'dojo/_base/lang',
    'dojo/query',
    'dojo/dom-geometry'
], function(declare, _WidgetBase, lang, query, domGeom) {
    'use strict';

    // Declare widget's prototype.
    return declare('GoogleCharts.widget.GoogleGaugeChart', [_WidgetBase], {

        // Parameters configured in the Modeler.
        animationDuration: null,
        animationEasing: null,
        forceIFrame: null,
        greenColor: null,
        greenFrom: null,
        greenTo: null,
        height: null,
        max: null,
        min: null,
        redColor: null,
        redFrom: null,
        redTo: null,
        width: null,
        yellowColor: null,
        yellowFrom: null,
        yellowTo: null,
        jsonDataSource: null,
        mfToExecute: null,

        // Internal variables. Non-primitives created in the prototype are shared between all widget instances.
        _handles: null,
        _contextObj: null,
        _alertDiv: null,
        _googleApiLoadScript: null,
        _googleVisualization: null,
        _startTime: null,
        _chartWrapper: null,
        _chartInitialized: false,
        _animationDuration: null,
        _greenColor: null,
        _greenFrom: null,
        _greenTo: null,
        _max: null,
        _min: null,
        _redColor: null,
        _redFrom: null,
        _redTo: null,
        _yellowColor: null,
        _yellowFrom: null,
        _yellowTo: null,

        resize: function(box) {
            if (this._chartWrapper !== null) {
                // Reset width to be able to shrink till 100.
                this._chartWrapper.setOption('width', 250);
                this._chartWrapper.draw();
                // Set chart width to parent width.
                var parentWidth = domGeom.getMarginSize(query(this.domNode).parent()[0]).w;
                if (parentWidth > 250) {
                    this._chartWrapper.setOption('width', parentWidth);
                    this._chartWrapper.draw();
                }
            }
        },

        _drawChartWithJson: function(callback) {
            logger.debug(this.id + "._drawChartWithJson");
            if (window._googleVisualizationLoading) {
                this._waitForGoogleLoad(callback);
            } else {
                var jsonString = this._contextObj ? this._contextObj.get(this.jsonDataSource) : "";
                var evalledData = eval('(' + jsonString + ')');
                var data = new google.visualization.arrayToDataTable(evalledData);
                if (this._chartInitialized === true) {
                    this._chartWrapper.setDataTable(data);
                    this._chartWrapper.draw();
                } else {
                    this._drawChart(data);
                    this._chartInitialized = true;
                }
                this._executeCallback(callback, "_drawChartWithJson");
            }
        },

        // We want to stop events on a mobile device
        _stopBubblingEventOnMobile: function (e) {
            logger.debug(this.id + "._stopBubblingEventOnMobile");
            if (typeof document.ontouchstart !== "undefined") {
                dojoEvent.stop(e);
            }
        },

        // Using Google ChartWrapper to draw the chart.
        _drawChart: function(data) {
            if (typeof data !== 'undefined' || data.trim() !== '') {
                // retrieve option values from data source.
                this._animationDuration = this._contextObj ? this._contextObj.get(this.animationDuration) : null;
                this._greenColor = this._contextObj ? this._contextObj.get(this.greenColor) : null;
                this._greenFrom = this._contextObj ? this._contextObj.get(this.greenFrom) : null;
                this._greenTo = this._contextObj ? this._contextObj.get(this.greenTo) : null;
                this._max = this._contextObj ? this._contextObj.get(this.max) : null;
                this._min = this._contextObj ? this._contextObj.get(this.min) : null;
                this._redColor = this._contextObj ? this._contextObj.get(this.redColor) : null;
                this._redFrom = this._contextObj ? this._contextObj.get(this.redFrom) : null;
                this._redTo = this._contextObj ? this._contextObj.get(this.redTo) : null;
                this._yellowColor = this._contextObj ? this._contextObj.get(this.yellowColor) : null;
                this._yellowFrom = this._contextObj ? this._contextObj.get(this.yellowFrom) : null;
                this._yellowTo = this._contextObj ? this._contextObj.get(this.yellowTo) : null;
                // set options
                var options = lang.mixin({}, {
                    'animation.duration': (this._animationDuration !== null) ? this._animationDuration : undefined,
                    'animation.easing': (this.animationEasing !== '') ? this.animationEasing : undefined,
                    'forceIFrame': (this.forceIFrame !== null) ? this.forceIFrame : undefined,
                    'greenColor': (this._greenColor !== null) ? this._greenColor : undefined,
                    'greenFrom': (this._greenFrom !== null) ? this._greenFrom : undefined,
                    'greenTo': (this._greenTo !== null) ? this._greenTo : undefined,
                    'height': (this.height > 0) ? this.height : undefined,
                    'max': (this._max !== null) ? this._max : undefined,
                    'min': (this._min !== null) ? this._min : undefined,
                    'redColor': (this._redColor !== null) ? this._redColor : undefined,
                    'redFrom': (this._redFrom !== null) ? this._redFrom : undefined,
                    'redTo': (this._redTo !== null) ? this._redTo : undefined,
                    'width': (this.width > 0) ? this.width : undefined,
                    'yellowColor': (this._yellowColor !== null) ? this._yellowColor : undefined,
                    'yellowFrom': (this._yellowFrom !== null) ? this._yellowFrom : undefined,
                    'yellowTo': (this._yellowTo !== null) ? this._yellowTo : undefined
                });
                // draw gauge
                this._chartWrapper = new google.visualization.ChartWrapper({
                    'chartType': 'Gauge',
                    'dataTable': data,
                    'options': options,
                    'containerId': this.id
                });
                this._chartWrapper.draw();
            } else {
                this._showError('No data for gauge.');
            }
            if (this.mfToExecute !== "") {
                this.domNode.style.cursor = "pointer";
            }
            this.connect(this.domNode, "click", function (e) {
                // Only on mobile stop event bubbling!
                this._stopBubblingEventOnMobile(e);
                // If a microflow has been set execute the microflow on a click.
                if (this.mfToExecute !== "") {
                    this._execMf(this.mfToExecute, this._contextObj.getGuid());
                }
            });
        },

        _execMf: function (mf, guid, cb) {
            logger.debug(this.id + "._execMf");
            if (mf && guid) {
                mx.ui.action(mf, {
                    params: {
                        applyto: "selection",
                        guids: [guid]
                    },
                    callback: function (objs) {
                        if (cb && typeof cb === "function") {
                            cb(objs);
                        }
                    },
                    error: function (error) {
                        console.debug(error.message);
                    }
                }, this);
            }
        }
    });
});
require(['GoogleCharts/widget/GoogleGaugeChart']);
