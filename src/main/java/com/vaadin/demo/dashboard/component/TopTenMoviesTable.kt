package com.vaadin.demo.dashboard.component

import com.github.vok.framework.vaadin.addStyleNames
import java.text.DecimalFormat
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

import com.vaadin.demo.dashboard.DashboardUI
import com.vaadin.demo.dashboard.domain.MovieRevenue
import com.vaadin.ui.themes.ValoTheme
import com.vaadin.v7.data.Property
import com.vaadin.v7.data.util.BeanItemContainer
import com.vaadin.v7.ui.Table

@Suppress("DEPRECATION")
class TopTenMoviesTable : Table() {

    override fun formatPropertyValue(rowId: Any?, colId: Any, property: Property<*>?): String {
        var result = super.formatPropertyValue(rowId, colId, property)
        if (colId == "revenue") {
            if (property?.value != null) {
                val r = property.value as Double
                val ret = DecimalFormat("#.##").format(r)
                result = "$$ret"
            } else {
                result = ""
            }
        }
        return result
    }

    init {
        caption = "Top 10 Titles by Revenue"

        addStyleNames(ValoTheme.TABLE_BORDERLESS, ValoTheme.TABLE_NO_STRIPES, ValoTheme.TABLE_NO_VERTICAL_LINES, ValoTheme.TABLE_SMALL)
        isSortEnabled = false
        setColumnAlignment("revenue", Table.Align.RIGHT)
        rowHeaderMode = Table.RowHeaderMode.INDEX
        columnHeaderMode = Table.ColumnHeaderMode.HIDDEN
        setSizeFull()

        val movieRevenues: List<MovieRevenue> = DashboardUI.getDataProvider().totalMovieRevenues.sortedBy { it.revenue }

        containerDataSource = BeanItemContainer(MovieRevenue::class.java, movieRevenues.subList(0, 10))

        setVisibleColumns("title", "revenue")
        setColumnHeaders("Title", "Revenue")
        setColumnExpandRatio("title", 2f)
        setColumnExpandRatio("revenue", 1f)

        sortContainerPropertyId = "revenue"
        isSortAscending = false
    }
}
