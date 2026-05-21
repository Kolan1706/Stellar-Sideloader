package vegabobo.dsusideloader.ui.screen.flash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.system.exitProcess
import kotlinx.coroutines.flow.collectLatest
import vegabobo.dsusideloader.ui.cards.DsuInfoCard
import vegabobo.dsusideloader.ui.cards.ImageSizeCard
import vegabobo.dsusideloader.ui.cards.UserdataCard
import vegabobo.dsusideloader.ui.cards.installation.InstallationCard
import vegabobo.dsusideloader.ui.cards.warnings.GrantingPermissionCard
import vegabobo.dsusideloader.ui.cards.warnings.RequiresLogPermissionCard
import vegabobo.dsusideloader.ui.cards.warnings.SetupStorage
import vegabobo.dsusideloader.ui.cards.warnings.StorageWarningCard
import vegabobo.dsusideloader.ui.cards.warnings.UnlockedBootloaderCard
import vegabobo.dsusideloader.ui.cards.warnings.UnsupportedCard
import vegabobo.dsusideloader.ui.components.stellarcards.InstallationParamsCard
import vegabobo.dsusideloader.ui.components.stellarcards.ThemeCustomizationCard
import vegabobo.dsusideloader.ui.screen.home.AdditionalCardState
import vegabobo.dsusideloader.ui.screen.home.HomeViewModel
import vegabobo.dsusideloader.ui.screen.home.SheetDisplayState
import vegabobo.dsusideloader.ui.sdialogs.CancelSheet
import vegabobo.dsusideloader.ui.sdialogs.ConfirmInstallationSheet
import vegabobo.dsusideloader.ui.sdialogs.DiscardDSUSheet
import vegabobo.dsusideloader.ui.sdialogs.ImageSizeWarningSheet
import vegabobo.dsusideloader.ui.sdialogs.ViewLogsBottomSheet
import vegabobo.dsusideloader.ui.util.KeepScreenOn
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    if (uiState.shouldKeepScreenOn) {
        KeepScreenOn()
    }

    LaunchedEffect(Unit) {
        homeViewModel.setupUserPreferences()
        homeViewModel.session.operationMode.collectLatest {
            homeViewModel.initialChecks()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ThemeCustomizationCard()

        if (!uiState.passedInitialChecks || uiState.additionalCard != AdditionalCardState.NONE) {
            when (uiState.additionalCard) {
                AdditionalCardState.NO_DYNAMIC_PARTITIONS ->
                    UnsupportedCard(
                        onClickClose = { exitProcess(0) },
                        onClickContinueAnyway = { homeViewModel.overrideDynamicPartitionCheck() },
                    )

                AdditionalCardState.SETUP_STORAGE ->
                    SetupStorage { homeViewModel.takeUriPermission(it) }

                AdditionalCardState.UNAVAIABLE_STORAGE ->
                    StorageWarningCard(
                        minPercentageFreeStorage = homeViewModel.allocPercentageInt.toString(),
                        onClick = { homeViewModel.overrideUnavaiableStorage() },
                    )

                AdditionalCardState.MISSING_READ_LOGS_PERMISSION ->
                    RequiresLogPermissionCard(
                        onClickGrant = { homeViewModel.grantReadLogs() },
                        onClickRefuse = { homeViewModel.refuseReadLogs() },
                    )

                AdditionalCardState.GRANTING_READ_LOGS_PERMISSION ->
                    GrantingPermissionCard()

                AdditionalCardState.BOOTLOADER_UNLOCKED_WARNING ->
                    UnlockedBootloaderCard { homeViewModel.onClickBootloaderUnlockedWarning() }

                AdditionalCardState.NONE -> {}
            }
        }

        if (uiState.passedInitialChecks && uiState.additionalCard == AdditionalCardState.NONE) {
            InstallationCard(
                uiState = uiState.installationCard,
                onClickInstall = { homeViewModel.onClickInstall() },
                onClickUnmountSdCardAndRetry = { homeViewModel.onClickUnmountSdCardAndRetry() },
                onClickSetSeLinuxPermissive = { homeViewModel.onClickSetSeLinuxPermissive() },
                onClickRetryInstallation = { homeViewModel.onClickRetryInstallation() },
                onClickClear = { homeViewModel.resetInstallationCard() },
                onSelectFileSuccess = { homeViewModel.onFileSelectionResult(it) },
                onClickCancelInstallation = { homeViewModel.onClickCancel() },
                onClickDiscardInstalledGsiAndInstall = { homeViewModel.onClickDiscardGsiAndStartInstallation() },
                onClickDiscardDsu = { homeViewModel.showDiscardSheet() },
                onClickRebootToDynOS = { homeViewModel.onClickRebootToDynOS() },
                onClickViewLogs = { homeViewModel.showLogsWarning() },
                onClickViewCommands = { /* ADB not in fork nav */ },
                minPercentageOfFreeStorage = homeViewModel.allocPercentageInt.toString(),
            )
            UserdataCard(
                isEnabled = uiState.isInstalling(),
                uiState = uiState.userDataCard,
                onCheckedChange = { homeViewModel.onCheckUserdataCard() },
                onValueChange = { homeViewModel.updateUserdataSize(it) },
            )
            ImageSizeCard(
                isEnabled = uiState.isInstalling(),
                uiState = uiState.imageSizeCard,
                onCheckedChange = { homeViewModel.onCheckImageSizeCard() },
                onValueChange = { homeViewModel.updateImageSize(it) },
            )
        }

        InstallationParamsCard()

        DsuInfoCard(
            onClickViewDocs = { /* uriHandler removed for simplicity */ },
            onClickLearnMore = { /* uriHandler removed for simplicity */ },
        )
    }

    when (uiState.sheetDisplay) {
        SheetDisplayState.CONFIRM_INSTALLATION ->
            ConfirmInstallationSheet(
                filename = homeViewModel.obtainSelectedFilename(),
                userdata = homeViewModel.session.userSelection.getUserDataSizeAsGB(),
                fileSize = homeViewModel.session.userSelection.userSelectedImageSize,
                onClickConfirm = { homeViewModel.onConfirmInstallationSheet() },
                onClickCancel = { homeViewModel.dismissSheet() },
            )

        SheetDisplayState.CANCEL_INSTALLATION ->
            CancelSheet(
                onClickConfirm = { homeViewModel.onClickCancelInstallationButton() },
                onClickCancel = { homeViewModel.dismissSheet() },
            )

        SheetDisplayState.IMAGESIZE_WARNING ->
            ImageSizeWarningSheet(
                onClickConfirm = { homeViewModel.dismissSheet() },
                onClickCancel = { homeViewModel.onCheckImageSizeCard() },
            )

        SheetDisplayState.DISCARD_DSU ->
            DiscardDSUSheet(
                onClickConfirm = { homeViewModel.onClickDiscardGsi() },
                onClickCancel = { homeViewModel.dismissSheet() },
            )

        SheetDisplayState.VIEW_LOGS ->
            ViewLogsBottomSheet(
                logs = uiState.installationLogs,
                onClickSaveLogs = { homeViewModel.saveLogs(it) },
                onDismiss = { homeViewModel.dismissSheet() },
            )

        SheetDisplayState.NONE -> {}
    }
}
