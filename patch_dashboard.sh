sed -i '/item {/,$!b;//!b;x;isEmpty;x;{
  /MissionObjectiveCard()/i \
        item {\
            CentralNavigationHub(viewModel)\
        }
}' app/src/main/java/com/example/ui/screens/DashboardScreen.kt
