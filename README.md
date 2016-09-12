# Google Charts (for Mendix)

Google charts are powerful, simple to use, and free. This package contains multiple widgets that are wrapping the "Google Charts" library so you can use it to visualize your aggregated data.

The widgets are compatible with Mendix 5.16.1 and up.

##### Available Charts:

- Area Chart
- Line Chart
- Column Chart
- Bar chart
- Pie Chart (with donut option)

## Configuration

The data for the widgets are retrieved through a (dataview) context object. The assumption here is that all aggregation of data is done in a microflow and a JSON string is send back. In the included test project you can see an example on how to do this.

### (Chart specific) Settings

All the settings for the widgets match the original "Google Chart" settings. The documentation for these can be found here: [Google Charts Documentation](https://developers.google.com/chart/interactive/docs/).

## Contributing

For more information on contributing to this repository visit [Contributing to a GitHub repository](https://world.mendix.com/display/howto50/Contributing+to+a+GitHub+repository)!
