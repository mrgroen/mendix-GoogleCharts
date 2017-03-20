define([
    'dojo/_base/declare',
    'mxui/widget/_WidgetBase',
    'dijit/_TemplatedMixin',
    'mxui/dom',
    'dojo/dom-class',
    'dojo/dom-construct',
    'dojo/dom-geometry',
    'dojo/query',
    'dojo/html',
    'dojo/_base/lang',
    'dojo/text!GoogleCharts/widget/template/GoogleChart.html',
    'GoogleCharts/lib/jsapi',
    'dojo/NodeList-traverse',
], function(declare, _WidgetBase, _TemplatedMixin, dom, domClass, domConstruct, domGeom, query, html, lang, widgetTemplate) {

    return declare('GoogleCharts.widget.base', [_WidgetBase, _TemplatedMixin], {

        templateString: widgetTemplate,

        update: function(obj, callback) {
            logger.debug(this.id + '.update');

            this._contextObj = obj;
            this._resetSubscriptions();
            this._updateRendering(callback);
        },

        // Rerender the interface.
        _updateRendering: function(callback) {
            logger.debug(this.id + "._updateRendering");
            // Draw or reload chart.
            if (this._contextObj !== null) {
                // Display widget dom node.
                domClass.toggle(this.domNode, 'hidden', false);

                if (!google) {
                    console.warn("Google JSAPI is not loaded!");
                    this._executeCallback(callback, "_updateRendering google not loaded");
                    return;
                }

                if (!google.visualization) {
                    if (!window._googleVisualizationLoading) {
                        window._googleVisualizationLoading = true;
                        if (google.loader && google.loader.Secure === false) {
                            google.loader.Secure = true;
                        }
                        google.load('visualization', '1', {
                            packages: [
                              'corechart',
                              'sankey'
                            ],
                            'callback': lang.hitch(this, function() {
                                window._googleVisualizationLoading = false;
                                this._drawChartWithJson(callback);
                            })
                        });
                    } else {
                        this._waitForGoogleLoad(callback);
                    }
                } else {
                    this._drawChartWithJson(callback);
                }
            } else {
                domClass.toggle(this.domNode, 'hidden', true);
            }

            // Important to clear all validations!
            this._clearValidations();
        },

        _waitForGoogleLoad: function(callback) {
            logger.debug(this.id + "._waitForGoogleLoad");
            var interval = null,
                i = 0,
                timeout = 5000; // We'll timeout if google is not loaded
            var intervalFunc = lang.hitch(this, function() {
                i++;
                if (i > timeout) {
                    logger.warn(this.id + "._waitForGoogleLoad: it seems Google is not loaded in the other widget. Quitting");
                    this._executeCallback(callback);
                    clearInterval(interval);
                }
                if (!window._googleVisualizationLoading) {
                    this._drawChartWithJson(callback);
                    clearInterval(interval);
                }
            });
            interval = setInterval(intervalFunc, 1);
        },

        // Draw chart with JSON input.
        _drawChartWithJson: function(callback) {
            logger.debug(this.id + "._drawChartWithJson");
            if (window._googleVisualizationLoading) {
                this._waitForGoogleLoad(callback);
            } else {
                var jsonString = this._contextObj ? this._contextObj.get(this.jsonDataSource) : "";
                var evalledData = eval('(' + jsonString + ')');
                var data = new google.visualization.DataTable(evalledData);
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

        resize: function(box) {
            if (this._chartWrapper !== null) {
                // Reset width to be able to shrink till 250.
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

        // Reset subscriptions.
        _resetSubscriptions: function() {
            logger.debug(this.id + "._resetSubscriptions");
            this.unsubscribeAll();

            // When a mendix object exists create subscribtions.
            if (this._contextObj) {
                this.subscribe({
                    guid: this._contextObj.getGuid(),
                    callback: lang.hitch(this, function(guid) {
                        logger.debug(this.id + "._subscription obj");
                        this._updateRendering();
                    })
                });

                this.subscribe({
                    guid: this._contextObj.getGuid(),
                    attr: this.jsonDataSource,
                    callback: lang.hitch(this, function(guid, attr, attrValue) {
                        logger.debug(this.id + "._subscription attr");
                        this._updateRendering();
                    })
                });

                this.subscribe({
                    guid: this._contextObj.getGuid(),
                    val: true,
                    callback: lang.hitch(this, this._handleValidation)
                });
            }
        },

        // Handle validations.
        _handleValidation: function(_validations) {
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
        _clearValidations: function() {
            domConstruct.destroy(this._alertdiv);
            this._alertdiv = null;
        },

        // Show an error message.
        _showError: function(message) {
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
        _addValidation: function(message) {
            this._showError(message);
        },

        _executeCallback: function(cb, from) {
            logger.debug(this.id + "._executeCallback" + (from ? " from " + from : ""));
            if (cb && typeof cb === "function") {
                cb();
            }
        }

    });

});
