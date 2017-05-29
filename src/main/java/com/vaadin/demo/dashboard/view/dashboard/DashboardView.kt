package com.vaadin.demo.dashboard.view.dashboard

import com.github.vok.framework.vaadin.*
import com.google.common.eventbus.Subscribe
import com.vaadin.demo.dashboard.DashboardUI
import com.vaadin.demo.dashboard.component.SparklineChart
import com.vaadin.demo.dashboard.component.TopGrossingMoviesChart
import com.vaadin.demo.dashboard.component.TopSixTheatersChart
import com.vaadin.demo.dashboard.component.TopTenMoviesTable
import com.vaadin.demo.dashboard.data.dummy.DummyDataGenerator
import com.vaadin.demo.dashboard.domain.DashboardNotification
import com.vaadin.demo.dashboard.event.DashboardEvent.CloseOpenWindowsEvent
import com.vaadin.demo.dashboard.event.DashboardEvent.NotificationsCountUpdatedEvent
import com.vaadin.demo.dashboard.event.DashboardEventBus
import com.vaadin.demo.dashboard.view.dashboard.DashboardEdit.DashboardEditListener
import com.vaadin.event.LayoutEvents.LayoutClickEvent
import com.vaadin.event.LayoutEvents.LayoutClickListener
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.server.FontAwesome
import com.vaadin.server.Responsive
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.Component
import com.vaadin.ui.CssLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.MenuBar
import com.vaadin.ui.MenuBar.Command
import com.vaadin.ui.MenuBar.MenuItem
import com.vaadin.ui.Notification
import com.vaadin.ui.Panel
import com.vaadin.ui.TextArea
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import com.vaadin.ui.themes.ValoTheme

class DashboardView : Panel(), View, DashboardEditListener {

    private lateinit var titleLabel: Label
    private lateinit var notificationsButton: NotificationsButton
    private lateinit var dashboardPanels: CssLayout
    private val root: VerticalLayout
    private var notificationsWindow: Window? = null

    init {
        addStyleName(ValoTheme.PANEL_BORDERLESS)
        setSizeFull()
        DashboardEventBus.register(this)

        root = VerticalLayout()
        root.setSizeFull()
        root.isSpacing = false
        root.addStyleName("dashboard-view")
        content = root
        Responsive.makeResponsive(root)

        root.addComponent(buildHeader())

        root.addComponent(buildSparklines())

        val content = buildContent()
        root.addComponent(content)
        root.setExpandRatio(content, 1f)

        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
        root.addLayoutClickListener { DashboardEventBus.post(CloseOpenWindowsEvent()) }
    }

    private fun buildSparklines(): Component {
        val sparks = CssLayout()
        sparks.addStyleName("sparks")
        sparks.setWidth("100%")
        Responsive.makeResponsive(sparks)

        var s = SparklineChart("Traffic", "K", "",
                DummyDataGenerator.chartColors[0], 22, 20, 80)
        sparks.addComponent(s)

        s = SparklineChart("Revenue / Day", "M", "$",
                DummyDataGenerator.chartColors[2], 8, 89, 150)
        sparks.addComponent(s)

        s = SparklineChart("Checkout Time", "s", "",
                DummyDataGenerator.chartColors[3], 10, 30, 120)
        sparks.addComponent(s)

        s = SparklineChart("Theater Fill Rate", "%", "",
                DummyDataGenerator.chartColors[5], 50, 34, 100)
        sparks.addComponent(s)

        return sparks
    }

    private fun buildHeader(): Component {
        val header = HorizontalLayout()
        header.addStyleName("viewheader")

        titleLabel = Label("Dashboard")
        titleLabel!!.id = TITLE_ID
        titleLabel!!.setSizeUndefined()
        titleLabel!!.addStyleName(ValoTheme.LABEL_H1)
        titleLabel!!.addStyleName(ValoTheme.LABEL_NO_MARGIN)
        header.addComponent(titleLabel)

        notificationsButton = buildNotificationsButton()
        val edit = buildEditButton()
        val tools = HorizontalLayout(notificationsButton, edit)
        tools.addStyleName("toolbar")
        header.addComponent(tools)

        return header
    }

    private fun buildNotificationsButton(): NotificationsButton {
        val result = NotificationsButton()
        result.addClickListener { event -> openNotificationsPopup(event) }
        return result
    }

    private fun buildEditButton(): Component {
        val result = Button()
        result.id = EDIT_ID
        result.icon = FontAwesome.EDIT
        result.addStyleName("icon-edit")
        result.addStyleName(ValoTheme.BUTTON_ICON_ONLY)
        result.description = "Edit Dashboard"
        result.addClickListener {
            ui.addWindow(
                    DashboardEdit(this@DashboardView, titleLabel!!
                            .value))
        }
        return result
    }

    private fun buildContent(): Component {
        dashboardPanels = CssLayout()
        dashboardPanels!!.addStyleName("dashboard-panels")
        Responsive.makeResponsive(dashboardPanels!!)

        dashboardPanels!!.addComponent(buildTopGrossingMovies())
        dashboardPanels!!.addComponent(buildNotes())
        dashboardPanels!!.addComponent(buildTop10TitlesByRevenue())
        dashboardPanels!!.addComponent(buildPopularMovies())

        return dashboardPanels
    }

    private fun buildTopGrossingMovies(): Component {
        val topGrossingMoviesChart = TopGrossingMoviesChart()
        topGrossingMoviesChart.setSizeFull()
        return createContentWrapper(topGrossingMoviesChart)
    }

    private fun buildNotes(): Component {
        val notes = TextArea("Notes")
        notes.value = "Remember to:\n· Zoom in and out in the Sales view\n· Filter the transactions and drag a set of them to the Reports tab\n· Create a new report\n· Change the schedule of the movie theater"
        notes.setSizeFull()
        notes.addStyleName(ValoTheme.TEXTAREA_BORDERLESS)
        val panel = createContentWrapper(notes)
        panel.addStyleName("notes")
        return panel
    }

    private fun buildTop10TitlesByRevenue(): Component {
        val contentWrapper = createContentWrapper(TopTenMoviesTable())
        contentWrapper.addStyleName("top10-revenue")
        return contentWrapper
    }

    private fun buildPopularMovies(): Component {
        return createContentWrapper(TopSixTheatersChart())
    }

    private fun createContentWrapper(content: Component): Component {
        val slot = CssLayout()
        slot.apply {
            w = fillParent; styleName = "dashboard-panel-slot"
            cssLayout { // card
                w = fillParent; styleName = ValoTheme.LAYOUT_CARD
                horizontalLayout { // toolbar
                    w = fillParent; styleName = "dashboard-panel-toolbar"; isSpacing = false

                    label(content.caption) { // caption
                        addStyleNames(ValoTheme.LABEL_H4, ValoTheme.LABEL_COLORED, ValoTheme.LABEL_NO_MARGIN)
                        expandRatio = 1f
                        alignment = Alignment.MIDDLE_LEFT
                    }
                    menuBar { // tools
                        styleName = ValoTheme.MENUBAR_BORDERLESS
                        val max = addItem("", FontAwesome.EXPAND, Command { selectedItem ->
                            if (!slot.styleName.contains("max")) {
                                selectedItem.icon = FontAwesome.COMPRESS
                                toggleMaximized(slot, true)
                            } else {
                                slot.removeStyleName("max")
                                selectedItem.icon = FontAwesome.EXPAND
                                toggleMaximized(slot, false)
                            }
                        })
                        max.styleName = "icon-only"
                        val root = addItem("", FontAwesome.COG, null)
                        root.addItem("Configure") { Notification.show("Not implemented in this demo") }
                        root.addSeparator()
                        root.addItem("Close") { Notification.show("Not implemented in this demo") }
                    }
                }
                content.caption = null
                addComponent(content)
            }
        }

        return slot
    }

    private fun openNotificationsPopup(event: ClickEvent) {
        val notificationsLayout = VerticalLayout()

        val title = Label("Notifications")
        title.addStyleName(ValoTheme.LABEL_H3)
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN)
        notificationsLayout.addComponent(title)

        val notifications = DashboardUI
                .getDataProvider().notifications
        DashboardEventBus.post(NotificationsCountUpdatedEvent())

        for (notification in notifications) {
            val notificationLayout = VerticalLayout()
            notificationLayout.setMargin(false)
            notificationLayout.isSpacing = false
            notificationLayout.addStyleName("notification-item")

            val titleLabel = Label(notification.firstName + " "
                    + notification.lastName + " "
                    + notification.action)
            titleLabel.addStyleName("notification-title")

            val timeLabel = Label(notification.prettyTime)
            timeLabel.addStyleName("notification-time")

            val contentLabel = Label(notification.content)
            contentLabel.addStyleName("notification-content")

            notificationLayout.addComponents(titleLabel, timeLabel,
                    contentLabel)
            notificationsLayout.addComponent(notificationLayout)
        }

        val footer = HorizontalLayout()
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR)
        footer.setWidth("100%")
        footer.isSpacing = false
        val showAll = Button("View All Notifications",
                ClickListener { Notification.show("Not implemented in this demo") })
        showAll.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
        showAll.addStyleName(ValoTheme.BUTTON_SMALL)
        footer.addComponent(showAll)
        footer.setComponentAlignment(showAll, Alignment.TOP_CENTER)
        notificationsLayout.addComponent(footer)

        if (notificationsWindow == null) {
            notificationsWindow = Window().apply {
                w = 300.px
                addStyleName("notifications")
                isClosable = false
                isResizable = false
                isDraggable = false
                setCloseShortcut(KeyCode.ESCAPE)
                content = notificationsLayout
            }
        }

        if (!notificationsWindow!!.isAttached) {
            notificationsWindow!!.positionY = event.clientY - event.relativeY + 40
            ui.addWindow(notificationsWindow!!)
            notificationsWindow!!.focus()
        } else {
            notificationsWindow!!.close()
        }
    }

    override fun enter(event: ViewChangeEvent) {
        notificationsButton.updateNotificationsCount(null)
    }

    override fun dashboardNameEdited(name: String) {
        titleLabel.value = name
    }

    private fun toggleMaximized(panel: Component, maximized: Boolean) {
        root.forEach { it.isVisible = !maximized }
        dashboardPanels.isVisible = true

        dashboardPanels.forEach { it.isVisible = !maximized }

        panel.toggleStyleName("max", maximized)
        if (maximized) panel.isVisible = true
    }

    companion object {

        @JvmStatic
        val EDIT_ID = "dashboard-edit"
        @JvmStatic
        val TITLE_ID = "dashboard-title"
    }

}

class NotificationsButton : Button() {
    init {
        icon = FontAwesome.BELL
        id = ID
        addStyleNames("notifications", ValoTheme.BUTTON_ICON_ONLY)
        DashboardEventBus.register(this)
    }

    @Subscribe
    fun updateNotificationsCount(
            event: NotificationsCountUpdatedEvent?) {
        setUnreadCount(DashboardUI.getDataProvider()
                .unreadNotificationsCount)
    }

    fun setUnreadCount(count: Int) {
        caption = count.toString()
        toggleStyleName(STYLE_UNREAD, count > 0)

        var description = "Notifications"
        if (count > 0) description += " ($count unread)"
        setDescription(description)
    }

    companion object {
        private val STYLE_UNREAD = "unread"
        val ID = "dashboard-notifications"
    }
}
