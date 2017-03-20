/*
    GoogleAreaChart
    ========================

    @file      : GoogleSankeyDiagram.js
    @version   : 1.0
    @author    : Jelte Lagendijk <jelte.lagendijk@mendix.com>
    @date      : Tue, 21 Feb 2017 12:29:36 GMT
    @copyright : Mendix
    @license   : Apache 2

    Documentation
    ========================
    A sankey diagram is a visualization used to depict a flow from one set of values to another.
    Google charts are powerful, simple to use, and free.
*/

define([
    'dojo/_base/declare',
    'GoogleCharts/widget/base',
    'dojo/_base/lang'
], function(declare, _WidgetBase, lang) {
    'use strict';

    // Declare widget's prototype.
    return declare('GoogleCharts.widget.GoogleSankeyDiagram', [_WidgetBase], {

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
                    'chartType': 'Sankey',
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
require(['GoogleCharts/widget/GoogleSankeyDiagram']);
