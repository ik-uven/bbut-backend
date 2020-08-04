package org.ikuven.bbut.tracking.participant;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ikuven.bbut.tracking.settings.BackendSettingsProperties;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ExcelService {

    private final BackendSettingsProperties backendSettingsProperties;

    public ExcelService(BackendSettingsProperties backendSettingsProperties) {
        this.backendSettingsProperties = backendSettingsProperties;
    }

    public ByteArrayInputStream exportToExcel(List<Participant> participants, List<Team> teams) throws IOException {

        try(XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            createParticipantResults(participants, workbook);
            createTeamResults(teams, workbook);

            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    private void createParticipantResults(List<Participant> participants, XSSFWorkbook workbook) {
        Sheet sheet = createSheet(workbook, getMaxLapNumber(participants));
        createHeaderRow(workbook, sheet, getMaxLapNumber(participants));
        createRows(workbook, sheet, participants);
    }

    private void createTeamResults(List<Team> teams, XSSFWorkbook workbook) {

        final var filteredTeams = teams.stream()
                .filter(team -> team.getParticipants().size() >= backendSettingsProperties.getTeams().getMinSize())
                .collect(Collectors.toList());

        if (!filteredTeams.isEmpty()) {
            final var participants = filteredTeams.stream()
                    .flatMap(team -> team.getParticipants().stream())
                    .collect(Collectors.toList());

            Sheet sheet = createTeamsSheet(workbook, getMaxLapNumber(participants));
            createTeamsTotalsHeaderRow(workbook, sheet);
            createTeamsTotalsRow(workbook, sheet, filteredTeams);
            createTeamsHeaderRow(workbook, sheet, getMaxLapNumber(participants), filteredTeams.size());
            createTeamRows(workbook, sheet, participants, filteredTeams.size());

        } else {
            Sheet sheet = createTeamsSheet(workbook, 0);
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue(String.format("Lagresultat visas endast om det finns lag med %d eller fler deltagare", backendSettingsProperties.getTeams().getMinSize()));
        }
    }

    private Sheet createSheet(XSSFWorkbook workbook, int numberOfLapColumns) {
        Sheet sheet = workbook.createSheet("Deltagare");
        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 6000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 3000);

        for (int i = 1; i <= numberOfLapColumns; i++) {
            sheet.setColumnWidth(5 + i, 1500);
        }

        return sheet;
    }

    private void createHeaderRow(XSSFWorkbook workbook, Sheet sheet, int maxLaps) {
        Row header = sheet.createRow(0);

        CellStyle headerStyle = getHeaderCellStyle(workbook);

        // Header cells
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("#");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Namn");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Klubb");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Lagnamn");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Status");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Varv");
        headerCell.setCellStyle(headerStyle);

        for (int i = 1; i <= maxLaps; i++) {
            headerCell = header.createCell(5 + i);
            headerCell.setCellValue(i);
            headerCell.setCellStyle(headerStyle);
        }
    }

    private void createRows(XSSFWorkbook workbook, Sheet sheet, List<Participant> participants) {

        CellStyle style = getRowCellStyle(workbook);
        CellStyle lapStyleOk = getLapOkCellStyle(workbook);
        CellStyle lapStyleNotOk = getLapNokCellStyle(workbook);

        int rownum = 1;
        for (Participant participant : participants) {
            Row row = sheet.createRow(rownum);
            Cell cell = row.createCell(0);
            cell.setCellValue(participant.getStartNumber());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(String.format("%s %s", participant.getFirstName(), participant.getLastName()));
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue(participant.getClub());
            cell.setCellStyle(style);

            cell = row.createCell(3);
            cell.setCellValue(participant.getTeam());
            cell.setCellStyle(style);

            cell = row.createCell(4);
            cell.setCellValue(participant.getParticipantState().toString());
            cell.setCellStyle(style);

            cell = row.createCell(5);
            cell.setCellValue(participant.getLastSuccessfulLapNumber());
            cell.setCellStyle(style);

            for (Lap lap : participant.getLaps()) {
                cell = row.createCell(5 + lap.getNumber());
                cell.setCellValue(getLapCellContent(lap, participant.getLastLap(), participant.getParticipantState()));
                cell.setCellStyle(lap.getState().equals(LapState.COMPLETED) ? lapStyleOk : lapStyleNotOk);
            }

            rownum++;
        }
    }

    private CellStyle getLapNokCellStyle(XSSFWorkbook workbook) {
        CellStyle lapStyleNotOk = workbook.createCellStyle();
        lapStyleNotOk.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        lapStyleNotOk.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        lapStyleNotOk.setAlignment(HorizontalAlignment.CENTER);
        setBorderStyles(lapStyleNotOk);
        return lapStyleNotOk;
    }

    private Sheet createTeamsSheet(XSSFWorkbook workbook, int numberOfLapColumns) {
        Sheet sheet = workbook.createSheet("Lag");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 2500);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);

        for (int i = 1; i <= numberOfLapColumns; i++) {
            sheet.setColumnWidth(4 + i, 1500);
        }

        return sheet;
    }

    private void createTeamsTotalsHeaderRow(XSSFWorkbook workbook, Sheet sheet) {
        Row header = sheet.createRow(0);

        CellStyle headerStyle = getHeaderCellStyle(workbook);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Lagnamn");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Totalt");
        headerCell.setCellStyle(headerStyle);
    }

    private void createTeamsTotalsRow(XSSFWorkbook workbook, Sheet sheet, List<Team> teams) {
        CellStyle style = getRowCellStyle(workbook);

        int rownum = 1;
        for (Team team : teams) {
            Row row = sheet.createRow(rownum);

            Cell cell = row.createCell(0);
            cell.setCellValue(team.getName());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(team.getTotalCompletedLaps());
            cell.setCellStyle(style);

            rownum++;
        }
    }

    private void createTeamsHeaderRow(XSSFWorkbook workbook, Sheet sheet, int maxLaps, int noOfTeams) {
        Row header = sheet.createRow(noOfTeams + 2);

        CellStyle headerStyle = getHeaderCellStyle(workbook);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Lagnamn");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("#");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Namn");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Status");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Varv");
        headerCell.setCellStyle(headerStyle);

        for (int i = 1; i <= maxLaps; i++) {
            headerCell = header.createCell(4 + i);
            headerCell.setCellValue(i);
            headerCell.setCellStyle(headerStyle);
        }
    }

    private void createTeamRows(XSSFWorkbook workbook, Sheet sheet, List<Participant> participants, int noOfTeams) {

        CellStyle style = getRowCellStyle(workbook);
        CellStyle lapStyleOk = getLapOkCellStyle(workbook);
        CellStyle lapStyleNotOk = getLapNokCellStyle(workbook);

        int rownum = noOfTeams + 3;
        for (Participant participant : participants) {
            Row row = sheet.createRow(rownum);
            Cell cell = row.createCell(0);
            cell.setCellValue(participant.getTeam());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(participant.getStartNumber());
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue(String.format("%s %s", participant.getFirstName(), participant.getLastName()));
            cell.setCellStyle(style);

            cell = row.createCell(3);
            cell.setCellValue(participant.getParticipantState().toString());
            cell.setCellStyle(style);

            cell = row.createCell(4);
            cell.setCellValue(participant.getLastSuccessfulLapNumber());
            cell.setCellStyle(style);

            for (Lap lap : participant.getLaps()) {
                cell = row.createCell(4 + lap.getNumber());
                cell.setCellValue(getLapCellContent(lap, participant.getLastLap(), participant.getParticipantState()));
                cell.setCellStyle(lap.getState().equals(LapState.COMPLETED) ? lapStyleOk : lapStyleNotOk);
            }

            rownum++;
        }
    }

    private CellStyle getLapOkCellStyle(XSSFWorkbook workbook) {
        CellStyle lapStyleOk = workbook.createCellStyle();
        lapStyleOk.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        lapStyleOk.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        lapStyleOk.setAlignment(HorizontalAlignment.CENTER);
        setBorderStyles(lapStyleOk);
        return lapStyleOk;
    }

    private CellStyle getRowCellStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(false);
        setBorderStyles(style);
        return style;
    }

    private CellStyle getHeaderCellStyle(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        setBorderStyles(headerStyle);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setBold(false);
        headerStyle.setFont(font);

        return headerStyle;
    }

    private int getMaxLapNumber(List<Participant> participants) {
        return participants.stream()
                .map(Participant::getLastLap)
                .filter(Objects::nonNull)
                .mapToInt(Lap::getNumber)
                .max()
                .orElse(0);
    }

    private void setBorderStyles(CellStyle cellStyle) {
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
    }

    private String getLapCellContent(Lap currentLap, Lap lastLap, ParticipantState participantState) {
        String content = " ";

        if (participantState.equals(ParticipantState.RESIGNED) && currentLap.getNumber() == lastLap.getNumber() || currentLap.getState().equals(LapState.OVERDUE)) {
            content = "⚑";
        }

        return content;
    }
}
