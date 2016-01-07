/*jslint white:true, nomen:true, plusplus:true, vars:true */
/*jshint browser:true */
/*global mx, define, require, browser, devel, console, google */
/*mendix */
/*
    GoogleAreaChart
    ========================

    @file      : GoogleLineChart.js
    @version   : 1.0
    @author    : Paul Ketelaars
    @date      : Fri, 19 Jun 2015 12:52:36 GMT
    @copyright : Ciber
    @license   : Apache 2

    Documentation
    ========================
    A line chart that is rendered within the browser using SVG or VML. Displays tooltips when hovering over points.
    Google charts are powerful, simple to use, and free.
*/

// Required module list. Remove unnecessary modules, you can always get them back from the boilerplate.
define([
    'dojo/_base/declare', 'mxui/widget/_WidgetBase', 'dijit/_TemplatedMixin',
    'mxui/dom', 'dojo/dom', 'dojo/query', 'dojo/dom-prop', 'dojo/dom-geometry', 'dojo/dom-class', 'dojo/dom-style', 'dojo/dom-construct', 'dojo/_base/array', 'dojo/_base/lang', 'dojo/html', 'dojo/_base/event',
    'GoogleCharts/lib/jquery-1.11.2', 'dojo/text!GoogleCharts/widget/template/GoogleChart.html'
], function (declare, _WidgetBase, _TemplatedMixin, dom, dojoDom, domQuery, domProp, domGeom, domClass, domStyle, domConstruct, dojoArray, lang, html, event, _jQuery, widgetTemplate) {
    'use strict';

    var $ = _jQuery.noConflict(true);
    
    // Declare widget's prototype.
    return declare('GoogleCharts.widget.GoogleLineChart', [_WidgetBase, _TemplatedMixin], {

        // _TemplatedMixin will create our dom node using this HTML template.
        templateString: widgetTemplate,

        // Parameters configured in the Modeler.
        title: "",
        backgroundColor: "",
        colors: "",
        enableInteractivity: null,
        forceIFrame: null,
        legend: "",
        aggregationTarget: "",
        tooltip: "",
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

        // dojo.declare.constructor is called to construct the widget instance. Implement to initialize non-primitive properties.
        constructor: function () {
            this._handles = [];
            if (!window._googleLoading || window._googleLoading === false) {
                window._googleLoading = true;
                this._googleApiLoadScript = dom.script({'src' : 'https://www.google.com/jsapi', 'id' : 'GoogleApiLoadScript'});
                document.getElementsByTagName('head')[0].appendChild(this._googleApiLoadScript);
            }
        },

        // dijit._WidgetBase.postCreate is called after constructing the widget. Implement to do extra setup work.
        postCreate: function () {
          console.debug(this.id + '.postCreate');
          this._updateRendering();
          this._setupEvents();
        },

        // mxui.widget._WidgetBase.update is called when context is changed or initialized. Implement to re-render and / or fetch data.
        update: function (obj, callback) {
            console.debug(this.id + '.update');

            this._contextObj = obj;
            this._resetSubscriptions();
            this._updateRendering();
			
			if (typeof callback !== 'undefined') {
				callback();
			}
        },

        // mxui.widget._WidgetBase.enable is called when the widget should enable editing. Implement to enable editing if widget is input widget.
        enable: function () {},

        // mxui.widget._WidgetBase.enable is called when the widget should disable editing. Implement to disable editing if widget is input widget.
        disable: function () {},

        // mxui.widget._WidgetBase.resize is called when the page's layout is recalculated. Implement to do sizing calculations. Prefer using CSS instead.
        resize: function (box) {
          if (this._chartWrapper !== null) {
            // Reset width to be able to shrink till 250.
            this._chartWrapper.setOption('width', 250);
            this._chartWrapper.draw();
            // Set chart width to parent width.
            var parentWidth = $('#' + this.id).parent().width();
            if (parentWidth > 250) {
              this._chartWrapper.setOption('width', parentWidth);
              this._chartWrapper.draw();
            }
          }
        },

        // mxui.widget._WidgetBase.uninitialize is called when the widget is destroyed. Implement to do special tear-down work.
        uninitialize: function () {
          // Clean up listeners, helper objects, etc. There is no need to remove listeners added with this.connect / this.subscribe / this.own.
        },
      
        // Using Google ChartWrapper to draw the chart.
        _drawChart: function (data) {
            if (typeof data !== 'undefined' || data.trim() !== '') {
              var options = $.extend({},{
                'animation': (this.animation !== false) ? {
                  'startup': this.animationStartup,
                  'duration': this.animationDuration,
                  'easing': (this.animationEasing !== '') ? this.animationEasing : undefined
                } : undefined,
                'title': (this.title !== '') ? this.title : undefined,
                'backgroundColor': (this.backgroundColor !== '') ? this.backgroundColor : undefined,
                'colors': (this.colors !== '') ? JSON.parse(this.colors) : undefined,
                'enableInteractivity': (this.enableInteractivity !== null) ? this.enableInteractivity : undefined,
                'forceIFrame': (this.forceIFrame !== null) ? this.forceIFrame : undefined,
                'legend': (this.legend !== '') ? JSON.parse(this.legend) : undefined,
                'tooltip': (this.tooltip !== '') ? JSON.parse(this.tooltip) : undefined,
                'aggregationTarget': (this.aggregationTarget !== '') ? this.aggregationTarget : undefined
              });
              this._chartWrapper = new google.visualization.ChartWrapper({
                'chartType': 'LineChart',
                'dataTable': data,
                'options': options,
                'containerId': this.id
              });
              this._chartWrapper.draw();
            } else {
              this._showError('No data for chart.');
            }
        },
      
        // Draw chart with JSON input.
        _drawChartWithJson: function () {
          var jsonString = this._contextObj ? this._contextObj.get(this.jsonDataSource) : "";
          var data = new google.visualization.DataTable(jsonString);
          if (this._chartInitialized === true) {
            this._chartWrapper.setDataTable(data);
            this._chartWrapper.draw();
          } else {
            this._drawChart(data);
            this._chartInitialized = true;
          }
        },
      
        // We want to stop events on a mobile device
        _stopBubblingEventOnMobile: function (e) {
            if (typeof document.ontouchstart !== 'undefined') {
                event.stop(e);
            }
        },

        // Attach events to HTML dom elements
        _setupEvents: function () {
          /*
            this.connect(this.colorSelectNode, 'change', function (e) {
                // Function from mendix object to set an attribute.
                this._contextObj.set(this.backgroundColor, this.colorSelectNode.value);
            });

            this.connect(this.infoTextNode, 'click', function (e) {

                // Only on mobile stop event bubbling!
                this._stopBubblingEventOnMobile(e);

                // If a microflow has been set execute the microflow on a click.
                if (this.mfToExecute !== '') {
                    mx.data.action({
                        params: {
                            applyto: 'selection',
                            actionname: this.mfToExecute,
                            guids: [this._contextObj.getGuid()]
                        },
                        callback: function (obj) {
                            //TODO what to do when all is ok!
                        },
                        error: lang.hitch(this, function (error) {
                            console.debug(this.id + ': An error occurred while executing microflow: ' + error.description);
                        })
                    }, this);
                }

            });
          */
        },

        // Rerender the interface.
        _updateRendering: function () {
          // Draw or reload chart.
          if (this._contextObj !== null) {
            // Display widget dom node.
            domStyle.set(this.domNode, 'display', 'block');
            if(!window._googleVisualization || window._googleVisualization === false) {
              this._googleVisualization = lang.hitch(this, function () {
                if (typeof google !== 'undefined') {
                  window._googleVisualization = true;
                  google.load('visualization', '1', {
                    'callback': lang.hitch(this,function(){
                      this._drawChartWithJson();
                    })
                  });
                } else {
                  var duration =  new Date().getTime() - this._startTime;
                  if (duration > 5000) {
                      console.warn('Timeout loading Google API.');
                      return;
                  }
                  setTimeout(this._googleVisualization,250);
                }
              });
              this._startTime = new Date().getTime();
              setTimeout(this._googleVisualization,100);
            } else {
              this._drawChartWithJson();
            }
          } else {
            // Hide widget dom node.
            domStyle.set(this.domNode, 'display', 'none');
          }

          // Important to clear all validations!
          this._clearValidations();
        },

        // Handle validations.
        _handleValidation: function (_validations) {
            this._clearValidations();

            var _validation = _validations[0],
                _message = _validation.getReasonByAttribute(this.jsonDataSource);

            if (this.readOnly) {
                _validation.removeAttribute(this.jsonDataSource);
            } else {
                if (_message) {
                    this._addValidation(_message);
                    _validation.removeAttribute(this.jsonDataSource);
                }
            }
        },

        // Clear validations.
        _clearValidations: function () {
            domConstruct.destroy(this._alertdiv);
            this._alertdiv = null;
        },

        // Show an error message.
        _showError: function (message) {
            console.log('[' + this.id + '] ERROR ' + message);
            if (this._alertDiv !== null) {
                html.set(this._alertDiv, message);
                return true;
            }
            this._alertDiv = domConstruct.create("div", {
                'class': 'alert alert-danger',
                'innerHTML': message
            });
            domConstruct.place(this.domNode, this._alertdiv);
        },

        // Add a validation.
        _addValidation: function (message) {
            this._showError(message);
        },

        // Reset subscriptions.
        _resetSubscriptions: function () {
            var _objectHandle = null,
                _attrHandle = null,
                _validationHandle = null;

            // Release handles on previous object, if any.
            if (this._handles) {
                dojoArray.forEach(this._handles, function (handle, i) {
                    mx.data.unsubscribe(handle);
                });
                this._handles = [];
            }

            // When a mendix object exists create subscribtions. 
            if (this._contextObj) {

                _objectHandle = this.subscribe({
                    guid: this._contextObj.getGuid(),
                    callback: lang.hitch(this, function (guid) {
                        this._updateRendering();
                    })
                });

                _attrHandle = this.subscribe({
                    guid: this._contextObj.getGuid(),
                    attr: this.jsonDataSource,
                    callback: lang.hitch(this, function (guid, attr, attrValue) {
                        this._updateRendering();
                    })
                });

                _validationHandle = this.subscribe({
                    guid: this._contextObj.getGuid(),
                    val: true,
                    callback: lang.hitch(this, this._handleValidation)
                });

                this._handles = [_objectHandle, _attrHandle, _validationHandle];
            }
        }
    });
});
require(['GoogleCharts/widget/GoogleLineChart'], function () {
    'use strict';
});