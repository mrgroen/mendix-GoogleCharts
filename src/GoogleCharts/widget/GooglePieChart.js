/*
    GooglePieChart
    ========================

    @file      : GooglePieChart.js
    @version   : 1.3.0
    @author    : Marcus Groen
    @date      : 20 March 2017
    @copyright : Incentro
    @license   : Apache 2

    Documentation
    ========================
    A pie chart that is rendered within the browser using SVG or VML. Displays tooltips when hovering over slices.
    Google charts are powerful, simple to use, and free.
*/

define([
    'dojo/_base/declare',
    'GoogleCharts/widget/base',
    'dojo/_base/lang'
], function(declare, _WidgetBase, lang) {
    'use strict';

    // Declare widget's prototype.
    return declare('GoogleCharts.widget.GooglePieChart', [_WidgetBase], {

        // Parameters configured in the Modeler.
        title: "",
        backgroundColor: "",
        colors: "",
        enableInteractivity: null,
        forceIFrame: null,
        is3D: null,
        legend: "",
        pieHole: "",
        pieSliceBorderColor: "",
        pieSliceText: "",
        pieSliceTextStyle: "",
        pieStartAngle: "",
        reverseCategories: "",
        slices: "",
        sliceVisibilityThreshold: "",
        tooltip: "",
        chartArea: "",
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
                    'title': (this.title !== '') ? this.title : undefined,
                    'backgroundColor': (this.backgroundColor !== '') ? this.backgroundColor : undefined,
                    'colors': (this.colors !== '') ? JSON.parse(this.colors) : undefined,
                    'enableInteractivity': (this.enableInteractivity !== null) ? this.enableInteractivity : undefined,
                    'forceIFrame': (this.forceIFrame !== null) ? this.forceIFrame : undefined,
                    'is3D': (this.is3D !== null) ? this.is3D : undefined,
                    'legend': (this.legend !== '') ? JSON.parse(this.legend) : undefined,
                    'pieHole': (this.pieHole !== '') ? this.pieHole : undefined,
                    'pieSliceBorderColor': (this.pieSliceBorderColor !== '') ? this.pieSliceBorderColor : undefined,
                    'pieSliceText': (this.pieSliceText !== '') ? this.pieSliceText : undefined,
                    'pieSliceTextStyle': (this.pieSliceTextStyle !== '') ? this.pieSliceTextStyle : undefined,
                    'pieStartAngle': (this.pieStartAngle !== '') ? this.pieStartAngle : undefined,
                    'reverseCategories': (this.reverseCategories !== '') ? this.reverseCategories : undefined,
                    'slices': (this.slices !== '') ? this.slices : undefined,
                    'sliceVisibilityThreshold': (this.sliceVisibilityThreshold !== null) ? this.sliceVisibilityThreshold : undefined,
                    'tooltip': (this.tooltip !== '') ? JSON.parse(this.tooltip) : undefined,
                    'chartArea': (this.chartArea !== '') ? JSON.parse(this.chartArea) : undefined,
                    'height': (this.height > 0) ? this.height : 400
                });
                this._chartWrapper = new google.visualization.ChartWrapper({
                    'chartType': 'PieChart',
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
require(['GoogleCharts/widget/GooglePieChart']);
