/*
    GoogleAreaChart
    ========================

    @file      : GoogleBubbleChart.js
    @version   : 1.3.0
    @author    : Marcus Groen
    @date      : 20 March 2017
    @copyright : Incentro
    @license   : Apache 2

    Documentation
    ========================
    A bubble chart that is rendered within the browser using SVG or VML. Displays tooltips when hovering over points.
    Google charts are powerful, simple to use, and free.
*/

define([
    'dojo/_base/declare',
    'GoogleCharts/widget/base',
    'dojo/_base/lang'
], function(declare, _WidgetBase, lang) {
    'use strict';

    // Declare widget's prototype.
    return declare('GoogleCharts.widget.GoogleBubbleChart', [_WidgetBase], {

        // Parameters configured in the Modeler.
        title: "",
        backgroundColor: "",
        colors: "",
        colorAxis: "",
        bubble: "",
        sortBubblesBySize: false,
        enableInteractivity: null,
        forceIFrame: null,
        legend: "",
        aggregationTarget: "",
        tooltip: "",
        sizeAxis: "",
        hAxis: "",
        vAxis: "",
        areaOpacity: "",
        animation: false,
        animationStartup: false,
        animationDuration: 0,
        animationEasing: "",
        jsonDataSource: "",
        mfToExecute: "",

        // Internal variables. Non-primitives created in the prototype are shared between all widget instances.
        _handles: null,
        _contextObj: null,
        _alertDiv: null,
        _googleApiLoadScript: null,
        _googleVisualization: null,
        _startTime: null,
        _chartWrapper: null,
        _chartInitialized: false,

        // Using Google ChartWrapper to draw the chart.
        _drawChart: function(data) {
            if (typeof data !== 'undefined' || data.trim() !== '') {
                var options = lang.mixin({}, {
                    'animation': (this.animation !== false) ? {
                        'startup': this.animationStartup,
                        'duration': this.animationDuration,
                        'easing': (this.animationEasing !== '') ? this.animationEasing : undefined
                    } : undefined,
                    'title': (this.title !== '') ? this.title : undefined,
                    'backgroundColor': (this.backgroundColor !== '') ? this.backgroundColor : undefined,
                    'colors': (this.colors !== '') ? JSON.parse(this.colors) : undefined,
                    'colorAxis': (this.colorAxis !== '') ? JSON.parse(this.colorAxis) : undefined,
                    'bubble': (this.bubble !== '') ? JSON.parse(this.bubble) : undefined,
                    'sortBubblesBySize': (this.sortBubblesBySize !== null) ? this.sortBubblesBySize : undefined,
                    'areaOpacity': (this.areaOpacity !== '') ? this.areaOpacity : undefined,
                    'enableInteractivity': (this.enableInteractivity !== null) ? this.enableInteractivity : undefined,
                    'forceIFrame': (this.forceIFrame !== null) ? this.forceIFrame : undefined,
                    'legend': (this.legend !== '') ? JSON.parse(this.legend) : undefined,
                    'tooltip': (this.tooltip !== '') ? JSON.parse(this.tooltip) : undefined,
                    'sizeAxis': (this.sizeAxis !== '') ? JSON.parse(this.sizeAxis) : undefined,
                    'hAxis': (this.hAxis !== '') ? JSON.parse(this.hAxis) : undefined,
                    'vAxis': (this.vAxis !== '') ? JSON.parse(this.vAxis) : undefined,
                    'aggregationTarget': (this.aggregationTarget !== '') ? this.aggregationTarget : undefined,
                    'height': (this.height > 0) ? this.height : 400
                });
                this._chartWrapper = new google.visualization.ChartWrapper({
                    'chartType': 'BubbleChart',
                    'dataTable': data,
                    'options': options,
                    'containerId': this.id
                });
                this._chartWrapper.draw();
            } else {
                this._showError('No data for chart.');
            }
        }
    });
});
require(['GoogleCharts/widget/GoogleBubbleChart']);
