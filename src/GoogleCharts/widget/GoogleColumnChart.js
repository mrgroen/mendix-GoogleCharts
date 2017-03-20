/*
    GoogleColumnChart
    ========================

    @file      : GoogleColumnChart.js
    @version   : 1.3.0
    @author    : Marcus Groen
    @date      : 20 March 2017
    @copyright : Incentro
    @license   : Apache 2

    Documentation
    ========================
    A column chart is a vertical bar chart rendered in the browser using SVG or VML, whichever is appropriate for the user's browser. Like all Google charts, column charts display tooltips when the user hovers over the data. For a horizontal version of this chart, see the bar chart.
    Google charts are powerful, simple to use, and free.
*/

define([
    'dojo/_base/declare',
    'GoogleCharts/widget/base',
    'dojo/_base/lang'
], function(declare, _WidgetBase, lang) {
    'use strict';

    // Declare widget's prototype.
    return declare('GoogleCharts.widget.GoogleColumnChart', [_WidgetBase], {

        // Parameters configured in the Modeler.
        title: "",
        backgroundColor: "",
        colors: "",
        enableInteractivity: null,
        forceIFrame: null,
        legend: "",
        tooltip: "",
        isStacked: "",
        vAxis: "",
        hAxis: "",
        dataOpacity: "",
        chartArea: "",
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
                    'dataOpacity': (this.dataOpacity !== '') ? this.dataOpacity : undefined,
                    'chartArea': (this.chartArea !== '') ? JSON.parse(this.chartArea) : undefined,
                    'enableInteractivity': (this.enableInteractivity !== null) ? this.enableInteractivity : undefined,
                    'forceIFrame': (this.forceIFrame !== null) ? this.forceIFrame : undefined,
                    'legend': (this.legend !== '') ? JSON.parse(this.legend) : undefined,
                    'tooltip': (this.tooltip !== '') ? JSON.parse(this.tooltip) : undefined,
                    'isStacked': (this.isStacked !== '') ? JSON.parse(this.isStacked) : undefined,
                    'vAxis': (this.vAxis !== '') ? JSON.parse(this.vAxis) : undefined,
                    'hAxis': (this.hAxis !== '') ? JSON.parse(this.hAxis) : undefined,
                    'height': (this.height > 0) ? this.height : 400
                });
                this._chartWrapper = new google.visualization.ChartWrapper({
                    'chartType': 'ColumnChart',
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
require(['GoogleCharts/widget/GoogleColumnChart']);
