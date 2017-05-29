package com.vaadin.demo.dashboard.component

import com.github.vok.framework.vaadin.*
import com.vaadin.addon.charts.Chart
import com.vaadin.addon.charts.model.*
import com.vaadin.addon.charts.model.style.Color
import com.vaadin.addon.charts.model.style.SolidColor
import com.vaadin.demo.dashboard.data.dummy.DummyDataGenerator
import com.vaadin.ui.Alignment
import com.vaadin.ui.HasComponents
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme

class SparklineChart(name: String, unit: String,
                     prefix: String, color: Color, howManyPoints: Int,
                     min: Int, max: Int) : VerticalLayout() {

    init {
        w = wrapContent; styleName = "spark"; isMargin = false; isSpacing = false
        defaultComponentAlignment = Alignment.TOP_CENTER

        val values: IntArray = DummyDataGenerator.randomSparklineValues(howManyPoints, min, max)

        label("$prefix${values[values.size - 1]}$unit") { // current
            w = wrapContent; styleName = ValoTheme.LABEL_HUGE
        }
        label(name) { // title
            w = wrapContent; addStyleNames(ValoTheme.LABEL_SMALL, ValoTheme.LABEL_LIGHT)
        }

        chart {
            w = 120.px; h = 40.px

            val series = DataSeries()
            values.forEach { series.add(DataSeriesItem("", it)) }

            configuration.apply {
                setTitle("")
                chart.type = ChartType.LINE
                chart.animation = false

                setSeries(series)
                series.configuration.apply {
                    legend = Legend()
                    legend.enabled = false
                }

                tooltip.enabled = false

                credits = Credits("")

                setPlotOptions(PlotOptionsLine().apply {
                    allowPointSelect = false
                    this.color = color
                    dataLabels = DataLabels(false)
                    lineWidth = 1
                    shadow = false
                    dashStyle = DashStyle.SOLID
                    marker = Marker(false)
                    enableMouseTracking = false
                    animation = false
                })

                getxAxis().apply {
                    labels = Labels(false)
                    tickWidth = 0
                    lineWidth = 0
                }
                getyAxis().apply {
                    title = AxisTitle("")
                    alternateGridColor = SolidColor(0, 0, 0, 0.0)  // transparent
                    labels = Labels(false)
                    lineWidth = 0
                    gridLineWidth = 0
                }
            }
        }

        label { // highLow
            html("High <b>${values.max()}</b> &nbsp;&nbsp;&nbsp; Low <b>${values.min()}</b>")
            addStyleNames(ValoTheme.LABEL_TINY, ValoTheme.LABEL_LIGHT)
            w = wrapContent
        }
    }
}

fun HasComponents.chart(block: Chart.()->Unit) = init(Chart(), block)
