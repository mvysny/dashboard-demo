package com.vaadin.demo.dashboard.component

import com.github.vok.framework.vaadin.*
import java.util.Arrays

import org.apache.commons.lang3.ArrayUtils

import com.vaadin.addon.charts.Chart
import com.vaadin.addon.charts.model.AxisTitle
import com.vaadin.addon.charts.model.ChartType
import com.vaadin.addon.charts.model.Configuration
import com.vaadin.addon.charts.model.Credits
import com.vaadin.addon.charts.model.DashStyle
import com.vaadin.addon.charts.model.DataLabels
import com.vaadin.addon.charts.model.DataSeries
import com.vaadin.addon.charts.model.DataSeriesItem
import com.vaadin.addon.charts.model.Labels
import com.vaadin.addon.charts.model.Legend
import com.vaadin.addon.charts.model.Marker
import com.vaadin.addon.charts.model.PlotOptionsLine
import com.vaadin.addon.charts.model.XAxis
import com.vaadin.addon.charts.model.YAxis
import com.vaadin.addon.charts.model.style.Color
import com.vaadin.addon.charts.model.style.SolidColor
import com.vaadin.demo.dashboard.data.dummy.DummyDataGenerator
import com.vaadin.shared.ui.ContentMode
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme

class SparklineChart(name: String, unit: String,
                     prefix: String, color: Color, howManyPoints: Int,
                     min: Int, max: Int) : VerticalLayout() {

    init {
        w = wrapContent; styleName = "spark"; isMargin = false; isSpacing = false
        defaultComponentAlignment = Alignment.TOP_CENTER

        val values = DummyDataGenerator.randomSparklineValues(howManyPoints,
                min, max)

        val current = Label(prefix + values[values.size - 1] + unit)
        current.setSizeUndefined()
        current.addStyleName(ValoTheme.LABEL_HUGE)
        addComponent(current)

        val title = Label(name)
        title.setSizeUndefined()
        title.addStyleName(ValoTheme.LABEL_SMALL)
        title.addStyleName(ValoTheme.LABEL_LIGHT)
        addComponent(title)

        addComponent(buildSparkline(values, color))

        val vals = Arrays.asList(*ArrayUtils.toObject(values))
        val highLow = Label(
                "High <b>" + java.util.Collections.max(vals)
                        + "</b> &nbsp;&nbsp;&nbsp; Low <b>"
                        + java.util.Collections.min(vals) + "</b>",
                ContentMode.HTML)
        highLow.addStyleName(ValoTheme.LABEL_TINY)
        highLow.addStyleName(ValoTheme.LABEL_LIGHT)
        highLow.setSizeUndefined()
        addComponent(highLow)

    }

    private fun buildSparkline(values: IntArray, color: Color): Component {
        val spark = Chart().apply {
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

        return spark
    }
}

fun HasComponents.chart(block: Chart.()->Unit) = init(Chart(), block)
