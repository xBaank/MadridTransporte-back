using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace MadridTransporte.Api.Migrations
{
    /// <inheritdoc />
    public partial class Initial : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Calendars",
                columns: table => new
                {
                    Id = table
                        .Column<int>(type: "integer", nullable: false)
                        .Annotation(
                            "Npgsql:ValueGenerationStrategy",
                            NpgsqlValueGenerationStrategy.IdentityByDefaultColumn
                        ),
                    ServiceId = table.Column<string>(type: "text", nullable: false),
                    Monday = table.Column<bool>(type: "boolean", nullable: false),
                    Tuesday = table.Column<bool>(type: "boolean", nullable: false),
                    Wednesday = table.Column<bool>(type: "boolean", nullable: false),
                    Thursday = table.Column<bool>(type: "boolean", nullable: false),
                    Friday = table.Column<bool>(type: "boolean", nullable: false),
                    Saturday = table.Column<bool>(type: "boolean", nullable: false),
                    Sunday = table.Column<bool>(type: "boolean", nullable: false),
                    StartDate = table.Column<long>(type: "bigint", nullable: false),
                    EndDate = table.Column<long>(type: "bigint", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Calendars", x => x.Id);
                }
            );

            migrationBuilder.CreateTable(
                name: "Itineraries",
                columns: table => new
                {
                    TripId = table.Column<string>(type: "text", nullable: false),
                    ItineraryCode = table.Column<string>(type: "text", nullable: false),
                    FullLineCode = table.Column<string>(type: "text", nullable: false),
                    Direction = table.Column<int>(type: "integer", nullable: false),
                    ServiceId = table.Column<string>(type: "text", nullable: false),
                    TripName = table.Column<string>(type: "text", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Itineraries", x => x.TripId);
                }
            );

            migrationBuilder.CreateTable(
                name: "Routes",
                columns: table => new
                {
                    FullLineCode = table.Column<string>(type: "text", nullable: false),
                    SimpleLineCode = table.Column<string>(type: "text", nullable: false),
                    RouteName = table.Column<string>(type: "text", nullable: false),
                    CodMode = table.Column<string>(type: "text", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Routes", x => x.FullLineCode);
                }
            );

            migrationBuilder.CreateTable(
                name: "Shapes",
                columns: table => new
                {
                    ItineraryId = table.Column<string>(type: "text", nullable: false),
                    Sequence = table.Column<int>(type: "integer", nullable: false),
                    Latitude = table.Column<double>(type: "double precision", nullable: false),
                    Longitude = table.Column<double>(type: "double precision", nullable: false),
                    Distance = table.Column<double>(type: "double precision", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Shapes", x => new { x.ItineraryId, x.Sequence });
                }
            );

            migrationBuilder.CreateTable(
                name: "StopInfos",
                columns: table => new
                {
                    CodigoEmpresa = table.Column<string>(type: "text", nullable: false),
                    IdEstacion = table.Column<string>(type: "text", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_StopInfos", x => new { x.IdEstacion, x.CodigoEmpresa });
                }
            );

            migrationBuilder.CreateTable(
                name: "StopOrders",
                columns: table => new
                {
                    TripId = table.Column<string>(type: "text", nullable: false),
                    Order = table.Column<int>(type: "integer", nullable: false),
                    FullStopCode = table.Column<string>(type: "text", nullable: false),
                    DepartureTime = table.Column<long>(type: "bigint", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_StopOrders", x => new { x.TripId, x.Order });
                }
            );

            migrationBuilder.CreateTable(
                name: "Stops",
                columns: table => new
                {
                    FullStopCode = table.Column<string>(type: "text", nullable: false),
                    StopCode = table.Column<string>(type: "text", nullable: false),
                    StopName = table.Column<string>(type: "text", nullable: false),
                    StopLat = table.Column<double>(type: "double precision", nullable: false),
                    StopLon = table.Column<double>(type: "double precision", nullable: false),
                    CodMode = table.Column<int>(type: "integer", nullable: false),
                    Wheelchair = table.Column<int>(type: "integer", nullable: false),
                    Zone = table.Column<string>(type: "text", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Stops", x => x.FullStopCode);
                }
            );

            migrationBuilder.CreateIndex(
                name: "IX_Calendars_ServiceId",
                table: "Calendars",
                column: "ServiceId"
            );

            migrationBuilder.CreateIndex(
                name: "IX_Itineraries_FullLineCode",
                table: "Itineraries",
                column: "FullLineCode"
            );

            migrationBuilder.CreateIndex(
                name: "IX_Itineraries_ItineraryCode",
                table: "Itineraries",
                column: "ItineraryCode"
            );

            migrationBuilder.CreateIndex(
                name: "IX_Itineraries_ServiceId",
                table: "Itineraries",
                column: "ServiceId"
            );

            migrationBuilder.CreateIndex(
                name: "IX_Routes_CodMode",
                table: "Routes",
                column: "CodMode"
            );

            migrationBuilder.CreateIndex(
                name: "IX_StopOrders_FullStopCode",
                table: "StopOrders",
                column: "FullStopCode"
            );

            migrationBuilder.CreateIndex(
                name: "IX_Stops_CodMode",
                table: "Stops",
                column: "CodMode"
            );
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(name: "Calendars");

            migrationBuilder.DropTable(name: "Itineraries");

            migrationBuilder.DropTable(name: "Routes");

            migrationBuilder.DropTable(name: "Shapes");

            migrationBuilder.DropTable(name: "StopInfos");

            migrationBuilder.DropTable(name: "StopOrders");

            migrationBuilder.DropTable(name: "Stops");
        }
    }
}
