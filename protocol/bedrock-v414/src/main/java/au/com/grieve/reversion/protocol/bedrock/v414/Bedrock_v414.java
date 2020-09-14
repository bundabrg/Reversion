/*
 * Reversion - Minecraft Protocol Support for Bedrock
 * Copyright (C) 2020 Reversion Developers
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package au.com.grieve.reversion.protocol.bedrock.v414;

import au.com.grieve.reversion.protocol.bedrock.v409.serializer.MoveEntityDeltaSerializer_v409;
import au.com.grieve.reversion.protocol.bedrock.v414.serializer.AddEntitySerializer_v414;
import au.com.grieve.reversion.protocol.bedrock.v414.serializer.ContainerCloseSerializer_v414;
import au.com.grieve.reversion.protocol.bedrock.v414.serializer.ItemStackResponseSerializer_v414;
import au.com.grieve.reversion.protocol.bedrock.v414.serializer.MovePlayerSerializer_v414;
import au.com.grieve.reversion.protocol.bedrock.v414.serializer.PlayerAuthInputSerializer_v414;
import au.com.grieve.reversion.protocol.bedrock.v414.serializer.ResourcePackStackSerializer_v414;
import au.com.grieve.reversion.protocol.bedrock.v414.serializer.SetEntityDataSerializer_v414;
import au.com.grieve.reversion.protocol.bedrock.v414.serializer.StartGameSerializer_v414;
import au.com.grieve.reversion.protocol.bedrock.v414.serializer.UpdateAttributesSerializer_v414;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.packet.AddBehaviorTreePacket;
import com.nukkitx.protocol.bedrock.packet.AddEntityPacket;
import com.nukkitx.protocol.bedrock.packet.AddItemEntityPacket;
import com.nukkitx.protocol.bedrock.packet.AddPaintingPacket;
import com.nukkitx.protocol.bedrock.packet.AddPlayerPacket;
import com.nukkitx.protocol.bedrock.packet.AdventureSettingsPacket;
import com.nukkitx.protocol.bedrock.packet.AnimatePacket;
import com.nukkitx.protocol.bedrock.packet.AnvilDamagePacket;
import com.nukkitx.protocol.bedrock.packet.AutomationClientConnectPacket;
import com.nukkitx.protocol.bedrock.packet.AvailableCommandsPacket;
import com.nukkitx.protocol.bedrock.packet.AvailableEntityIdentifiersPacket;
import com.nukkitx.protocol.bedrock.packet.BiomeDefinitionListPacket;
import com.nukkitx.protocol.bedrock.packet.BlockEntityDataPacket;
import com.nukkitx.protocol.bedrock.packet.BlockEventPacket;
import com.nukkitx.protocol.bedrock.packet.BlockPickRequestPacket;
import com.nukkitx.protocol.bedrock.packet.BookEditPacket;
import com.nukkitx.protocol.bedrock.packet.BossEventPacket;
import com.nukkitx.protocol.bedrock.packet.CameraPacket;
import com.nukkitx.protocol.bedrock.packet.ChangeDimensionPacket;
import com.nukkitx.protocol.bedrock.packet.ChunkRadiusUpdatedPacket;
import com.nukkitx.protocol.bedrock.packet.ClientCacheBlobStatusPacket;
import com.nukkitx.protocol.bedrock.packet.ClientCacheMissResponsePacket;
import com.nukkitx.protocol.bedrock.packet.ClientCacheStatusPacket;
import com.nukkitx.protocol.bedrock.packet.ClientToServerHandshakePacket;
import com.nukkitx.protocol.bedrock.packet.ClientboundMapItemDataPacket;
import com.nukkitx.protocol.bedrock.packet.CodeBuilderPacket;
import com.nukkitx.protocol.bedrock.packet.CommandBlockUpdatePacket;
import com.nukkitx.protocol.bedrock.packet.CommandOutputPacket;
import com.nukkitx.protocol.bedrock.packet.CommandRequestPacket;
import com.nukkitx.protocol.bedrock.packet.CompletedUsingItemPacket;
import com.nukkitx.protocol.bedrock.packet.ContainerClosePacket;
import com.nukkitx.protocol.bedrock.packet.ContainerOpenPacket;
import com.nukkitx.protocol.bedrock.packet.ContainerSetDataPacket;
import com.nukkitx.protocol.bedrock.packet.CraftingDataPacket;
import com.nukkitx.protocol.bedrock.packet.CraftingEventPacket;
import com.nukkitx.protocol.bedrock.packet.CreativeContentPacket;
import com.nukkitx.protocol.bedrock.packet.DebugInfoPacket;
import com.nukkitx.protocol.bedrock.packet.DisconnectPacket;
import com.nukkitx.protocol.bedrock.packet.EducationSettingsPacket;
import com.nukkitx.protocol.bedrock.packet.EmoteListPacket;
import com.nukkitx.protocol.bedrock.packet.EmotePacket;
import com.nukkitx.protocol.bedrock.packet.EntityEventPacket;
import com.nukkitx.protocol.bedrock.packet.EntityFallPacket;
import com.nukkitx.protocol.bedrock.packet.EntityPickRequestPacket;
import com.nukkitx.protocol.bedrock.packet.EventPacket;
import com.nukkitx.protocol.bedrock.packet.GameRulesChangedPacket;
import com.nukkitx.protocol.bedrock.packet.GuiDataPickItemPacket;
import com.nukkitx.protocol.bedrock.packet.HurtArmorPacket;
import com.nukkitx.protocol.bedrock.packet.InteractPacket;
import com.nukkitx.protocol.bedrock.packet.InventoryContentPacket;
import com.nukkitx.protocol.bedrock.packet.InventorySlotPacket;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;
import com.nukkitx.protocol.bedrock.packet.ItemFrameDropItemPacket;
import com.nukkitx.protocol.bedrock.packet.ItemStackRequestPacket;
import com.nukkitx.protocol.bedrock.packet.ItemStackResponsePacket;
import com.nukkitx.protocol.bedrock.packet.LabTablePacket;
import com.nukkitx.protocol.bedrock.packet.LecternUpdatePacket;
import com.nukkitx.protocol.bedrock.packet.LevelChunkPacket;
import com.nukkitx.protocol.bedrock.packet.LevelEventGenericPacket;
import com.nukkitx.protocol.bedrock.packet.LevelEventPacket;
import com.nukkitx.protocol.bedrock.packet.LevelSoundEvent1Packet;
import com.nukkitx.protocol.bedrock.packet.LevelSoundEvent2Packet;
import com.nukkitx.protocol.bedrock.packet.LevelSoundEventPacket;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.packet.MapCreateLockedCopyPacket;
import com.nukkitx.protocol.bedrock.packet.MapInfoRequestPacket;
import com.nukkitx.protocol.bedrock.packet.MobArmorEquipmentPacket;
import com.nukkitx.protocol.bedrock.packet.MobEffectPacket;
import com.nukkitx.protocol.bedrock.packet.MobEquipmentPacket;
import com.nukkitx.protocol.bedrock.packet.ModalFormRequestPacket;
import com.nukkitx.protocol.bedrock.packet.ModalFormResponsePacket;
import com.nukkitx.protocol.bedrock.packet.MoveEntityAbsolutePacket;
import com.nukkitx.protocol.bedrock.packet.MoveEntityDeltaPacket;
import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import com.nukkitx.protocol.bedrock.packet.MultiplayerSettingsPacket;
import com.nukkitx.protocol.bedrock.packet.NetworkChunkPublisherUpdatePacket;
import com.nukkitx.protocol.bedrock.packet.NetworkSettingsPacket;
import com.nukkitx.protocol.bedrock.packet.NetworkStackLatencyPacket;
import com.nukkitx.protocol.bedrock.packet.NpcRequestPacket;
import com.nukkitx.protocol.bedrock.packet.OnScreenTextureAnimationPacket;
import com.nukkitx.protocol.bedrock.packet.PacketViolationWarningPacket;
import com.nukkitx.protocol.bedrock.packet.PhotoTransferPacket;
import com.nukkitx.protocol.bedrock.packet.PlaySoundPacket;
import com.nukkitx.protocol.bedrock.packet.PlayStatusPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerArmorDamagePacket;
import com.nukkitx.protocol.bedrock.packet.PlayerAuthInputPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerEnchantOptionsPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerHotbarPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerInputPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerListPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerSkinPacket;
import com.nukkitx.protocol.bedrock.packet.PositionTrackingDBClientRequestPacket;
import com.nukkitx.protocol.bedrock.packet.PositionTrackingDBServerBroadcastPacket;
import com.nukkitx.protocol.bedrock.packet.PurchaseReceiptPacket;
import com.nukkitx.protocol.bedrock.packet.RemoveEntityPacket;
import com.nukkitx.protocol.bedrock.packet.RemoveObjectivePacket;
import com.nukkitx.protocol.bedrock.packet.RequestChunkRadiusPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackChunkDataPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackChunkRequestPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackClientResponsePacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackDataInfoPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackStackPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePacksInfoPacket;
import com.nukkitx.protocol.bedrock.packet.RespawnPacket;
import com.nukkitx.protocol.bedrock.packet.RiderJumpPacket;
import com.nukkitx.protocol.bedrock.packet.ScriptCustomEventPacket;
import com.nukkitx.protocol.bedrock.packet.ServerSettingsRequestPacket;
import com.nukkitx.protocol.bedrock.packet.ServerSettingsResponsePacket;
import com.nukkitx.protocol.bedrock.packet.ServerToClientHandshakePacket;
import com.nukkitx.protocol.bedrock.packet.SetCommandsEnabledPacket;
import com.nukkitx.protocol.bedrock.packet.SetDefaultGameTypePacket;
import com.nukkitx.protocol.bedrock.packet.SetDifficultyPacket;
import com.nukkitx.protocol.bedrock.packet.SetDisplayObjectivePacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityLinkPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityMotionPacket;
import com.nukkitx.protocol.bedrock.packet.SetHealthPacket;
import com.nukkitx.protocol.bedrock.packet.SetLastHurtByPacket;
import com.nukkitx.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;
import com.nukkitx.protocol.bedrock.packet.SetPlayerGameTypePacket;
import com.nukkitx.protocol.bedrock.packet.SetScorePacket;
import com.nukkitx.protocol.bedrock.packet.SetScoreboardIdentityPacket;
import com.nukkitx.protocol.bedrock.packet.SetSpawnPositionPacket;
import com.nukkitx.protocol.bedrock.packet.SetTimePacket;
import com.nukkitx.protocol.bedrock.packet.SetTitlePacket;
import com.nukkitx.protocol.bedrock.packet.SettingsCommandPacket;
import com.nukkitx.protocol.bedrock.packet.ShowCreditsPacket;
import com.nukkitx.protocol.bedrock.packet.ShowProfilePacket;
import com.nukkitx.protocol.bedrock.packet.ShowStoreOfferPacket;
import com.nukkitx.protocol.bedrock.packet.SimpleEventPacket;
import com.nukkitx.protocol.bedrock.packet.SpawnExperienceOrbPacket;
import com.nukkitx.protocol.bedrock.packet.SpawnParticleEffectPacket;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;
import com.nukkitx.protocol.bedrock.packet.StopSoundPacket;
import com.nukkitx.protocol.bedrock.packet.StructureBlockUpdatePacket;
import com.nukkitx.protocol.bedrock.packet.StructureTemplateDataRequestPacket;
import com.nukkitx.protocol.bedrock.packet.StructureTemplateDataResponsePacket;
import com.nukkitx.protocol.bedrock.packet.SubClientLoginPacket;
import com.nukkitx.protocol.bedrock.packet.TakeItemEntityPacket;
import com.nukkitx.protocol.bedrock.packet.TextPacket;
import com.nukkitx.protocol.bedrock.packet.TickSyncPacket;
import com.nukkitx.protocol.bedrock.packet.TransferPacket;
import com.nukkitx.protocol.bedrock.packet.UpdateAttributesPacket;
import com.nukkitx.protocol.bedrock.packet.UpdateBlockPacket;
import com.nukkitx.protocol.bedrock.packet.UpdateBlockPropertiesPacket;
import com.nukkitx.protocol.bedrock.packet.UpdateBlockSyncedPacket;
import com.nukkitx.protocol.bedrock.packet.UpdateEquipPacket;
import com.nukkitx.protocol.bedrock.packet.UpdatePlayerGameTypePacket;
import com.nukkitx.protocol.bedrock.packet.UpdateSoftEnumPacket;
import com.nukkitx.protocol.bedrock.packet.UpdateTradePacket;
import com.nukkitx.protocol.bedrock.packet.VideoStreamConnectPacket;
import com.nukkitx.protocol.bedrock.v291.serializer.AddBehaviorTreeSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.AddItemEntitySerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.AdventureSettingsSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.AnimateSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.AutomationClientConnectSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.BlockEntityDataSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.BlockEventSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.BlockPickRequestSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.BookEditSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.BossEventSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.CameraSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ChangeDimensionSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ChunkRadiusUpdatedSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ClientToServerHandshakeSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.CommandOutputSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.CommandRequestSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ContainerOpenSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ContainerSetDataSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.CraftingEventSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.DisconnectSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.EntityEventSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.EntityFallSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.EntityPickRequestSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.GameRulesChangedSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.GuiDataPickItemSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ItemFrameDropItemSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.LabTableSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.LevelEventSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.LevelSoundEvent1Serializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.LoginSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.MapInfoRequestSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.MobArmorEquipmentSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.MobEffectSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.MobEquipmentSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ModalFormRequestSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ModalFormResponseSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.MoveEntityAbsoluteSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.NpcRequestSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.PhotoTransferSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.PlaySoundSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.PlayStatusSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.PlayerActionSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.PlayerHotbarSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.PlayerInputSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.PurchaseReceiptSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.RemoveEntitySerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.RemoveObjectiveSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.RequestChunkRadiusSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ResourcePackChunkRequestSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ResourcePackClientResponseSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.RiderJumpSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ScriptCustomEventSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ServerSettingsRequestSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ServerSettingsResponseSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ServerToClientHandshakeSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetCommandsEnabledSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetDefaultGameTypeSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetDifficultySerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetDisplayObjectiveSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetEntityLinkSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetEntityMotionSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetHealthSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetLastHurtBySerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetLocalPlayerAsInitializedSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetPlayerGameTypeSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetScoreSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetScoreboardIdentitySerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetTimeSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SetTitleSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ShowCreditsSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ShowProfileSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.ShowStoreOfferSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SimpleEventSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SpawnExperienceOrbSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.StopSoundSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.SubClientLoginSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.TakeItemEntitySerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.TransferSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.UpdateBlockSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.UpdateBlockSyncedSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.UpdateEquipSerializer_v291;
import com.nukkitx.protocol.bedrock.v291.serializer.UpdateSoftEnumSerializer_v291;
import com.nukkitx.protocol.bedrock.v313.serializer.AvailableEntityIdentifiersSerializer_v313;
import com.nukkitx.protocol.bedrock.v313.serializer.BiomeDefinitionListSerializer_v313;
import com.nukkitx.protocol.bedrock.v313.serializer.NetworkChunkPublisherUpdateSerializer_v313;
import com.nukkitx.protocol.bedrock.v332.serializer.NetworkStackLatencySerializer_v332;
import com.nukkitx.protocol.bedrock.v332.serializer.ResourcePacksInfoSerializer_v332;
import com.nukkitx.protocol.bedrock.v332.serializer.SpawnParticleEffectSerializer_v332;
import com.nukkitx.protocol.bedrock.v332.serializer.TextSerializer_v332;
import com.nukkitx.protocol.bedrock.v354.serializer.ClientboundMapItemDataSerializer_v354;
import com.nukkitx.protocol.bedrock.v354.serializer.LecternUpdateSerializer_v354;
import com.nukkitx.protocol.bedrock.v354.serializer.MapCreateLockedCopySerializer_v354;
import com.nukkitx.protocol.bedrock.v354.serializer.OnScreenTextureAnimationSerializer_v354;
import com.nukkitx.protocol.bedrock.v354.serializer.UpdateTradeSerializer_v354;
import com.nukkitx.protocol.bedrock.v361.serializer.AddPaintingSerializer_v361;
import com.nukkitx.protocol.bedrock.v361.serializer.ClientCacheBlobStatusSerializer_v361;
import com.nukkitx.protocol.bedrock.v361.serializer.ClientCacheMissResponseSerializer_v361;
import com.nukkitx.protocol.bedrock.v361.serializer.ClientCacheStatusSerializer_v361;
import com.nukkitx.protocol.bedrock.v361.serializer.CommandBlockUpdateSerializer_v361;
import com.nukkitx.protocol.bedrock.v361.serializer.LevelChunkSerializer_v361;
import com.nukkitx.protocol.bedrock.v361.serializer.LevelEventGenericSerializer_v361;
import com.nukkitx.protocol.bedrock.v361.serializer.ResourcePackDataInfoSerializer_v361;
import com.nukkitx.protocol.bedrock.v361.serializer.StructureTemplateDataRequestSerializer_v361;
import com.nukkitx.protocol.bedrock.v361.serializer.UpdateBlockPropertiesSerializer_v361;
import com.nukkitx.protocol.bedrock.v361.serializer.VideoStreamConnectSerializer_v361;
import com.nukkitx.protocol.bedrock.v388.serializer.AddPlayerSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.AnvilDamageSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.AvailableCommandsSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.CompletedUsingItemSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.EducationSettingsSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.EmoteSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.EventSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.InteractSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.MultiplayerSettingsSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.NetworkSettingsSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.ResourcePackChunkDataSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.RespawnSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.SettingsCommandSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.StructureBlockUpdateSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.StructureTemplateDataResponseSerializer_v388;
import com.nukkitx.protocol.bedrock.v388.serializer.TickSyncSerializer_v388;
import com.nukkitx.protocol.bedrock.v390.serializer.PlayerListSerializer_v390;
import com.nukkitx.protocol.bedrock.v390.serializer.PlayerSkinSerializer_v390;
import com.nukkitx.protocol.bedrock.v407.serializer.CodeBuilderSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.CraftingDataSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.CreativeContentSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.DebugInfoSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.EmoteListSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.HurtArmorSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.InventoryContentSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.InventorySlotSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.InventoryTransactionSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.ItemStackRequestSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.LevelSoundEvent2Serializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.LevelSoundEventSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.PacketViolationWarningSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.PlayerArmorDamageSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.PlayerEnchantOptionsSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.PositionTrackingDBClientRequestSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.PositionTrackingDBServerBroadcastSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.SetSpawnPositionSerializer_v407;
import com.nukkitx.protocol.bedrock.v407.serializer.UpdatePlayerGameTypeSerializer_v407;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Bedrock_v414 {
    public BedrockPacketCodec V414_CODEC = BedrockPacketCodec.builder()
            .protocolVersion(414)
            .minecraftVersion("1.16.100.55")
            .helper(BedrockPacketHelper_v414.INSTANCE)
            .registerPacket(LoginPacket.class, LoginSerializer_v291.INSTANCE, 1)
            .registerPacket(PlayStatusPacket.class, PlayStatusSerializer_v291.INSTANCE, 2)
            .registerPacket(ServerToClientHandshakePacket.class, ServerToClientHandshakeSerializer_v291.INSTANCE, 3)
            .registerPacket(ClientToServerHandshakePacket.class, ClientToServerHandshakeSerializer_v291.INSTANCE, 4)
            .registerPacket(DisconnectPacket.class, DisconnectSerializer_v291.INSTANCE, 5)
            .registerPacket(ResourcePacksInfoPacket.class, ResourcePacksInfoSerializer_v332.INSTANCE, 6)
            .registerPacket(ResourcePackStackPacket.class, ResourcePackStackSerializer_v414.INSTANCE, 7)
            .registerPacket(ResourcePackClientResponsePacket.class, ResourcePackClientResponseSerializer_v291.INSTANCE, 8)
            .registerPacket(TextPacket.class, TextSerializer_v332.INSTANCE, 9)
            .registerPacket(SetTimePacket.class, SetTimeSerializer_v291.INSTANCE, 10)
            .registerPacket(StartGamePacket.class, StartGameSerializer_v414.INSTANCE, 11)
            .registerPacket(AddPlayerPacket.class, AddPlayerSerializer_v388.INSTANCE, 12)
            .registerPacket(AddEntityPacket.class, AddEntitySerializer_v414.INSTANCE, 13)
            .registerPacket(RemoveEntityPacket.class, RemoveEntitySerializer_v291.INSTANCE, 14)
            .registerPacket(AddItemEntityPacket.class, AddItemEntitySerializer_v291.INSTANCE, 15)
            .registerPacket(TakeItemEntityPacket.class, TakeItemEntitySerializer_v291.INSTANCE, 17)
            .registerPacket(MoveEntityAbsolutePacket.class, MoveEntityAbsoluteSerializer_v291.INSTANCE, 18)
            .registerPacket(MovePlayerPacket.class, MovePlayerSerializer_v414.INSTANCE, 19)
            .registerPacket(RiderJumpPacket.class, RiderJumpSerializer_v291.INSTANCE, 20)
            .registerPacket(UpdateBlockPacket.class, UpdateBlockSerializer_v291.INSTANCE, 21)
            .registerPacket(AddPaintingPacket.class, AddPaintingSerializer_v361.INSTANCE, 22)
            .registerPacket(TickSyncPacket.class, TickSyncSerializer_v388.INSTANCE, 23)
            .registerPacket(LevelSoundEvent1Packet.class, LevelSoundEvent1Serializer_v291.INSTANCE, 24)
            .registerPacket(LevelEventPacket.class, LevelEventSerializer_v291.INSTANCE, 25)
            .registerPacket(BlockEventPacket.class, BlockEventSerializer_v291.INSTANCE, 26)
            .registerPacket(EntityEventPacket.class, EntityEventSerializer_v291.INSTANCE, 27)
            .registerPacket(MobEffectPacket.class, MobEffectSerializer_v291.INSTANCE, 28)
            .registerPacket(UpdateAttributesPacket.class, UpdateAttributesSerializer_v414.INSTANCE, 29)
            .registerPacket(InventoryTransactionPacket.class, InventoryTransactionSerializer_v407.INSTANCE, 30)
            .registerPacket(MobEquipmentPacket.class, MobEquipmentSerializer_v291.INSTANCE, 31)
            .registerPacket(MobArmorEquipmentPacket.class, MobArmorEquipmentSerializer_v291.INSTANCE, 32)
            .registerPacket(InteractPacket.class, InteractSerializer_v388.INSTANCE, 33)
            .registerPacket(BlockPickRequestPacket.class, BlockPickRequestSerializer_v291.INSTANCE, 34)
            .registerPacket(EntityPickRequestPacket.class, EntityPickRequestSerializer_v291.INSTANCE, 35)
            .registerPacket(PlayerActionPacket.class, PlayerActionSerializer_v291.INSTANCE, 36)
            .registerPacket(EntityFallPacket.class, EntityFallSerializer_v291.INSTANCE, 37)
            .registerPacket(HurtArmorPacket.class, HurtArmorSerializer_v407.INSTANCE, 38)
            .registerPacket(SetEntityDataPacket.class, SetEntityDataSerializer_v414.INSTANCE, 39)
            .registerPacket(SetEntityMotionPacket.class, SetEntityMotionSerializer_v291.INSTANCE, 40)
            .registerPacket(SetEntityLinkPacket.class, SetEntityLinkSerializer_v291.INSTANCE, 41)
            .registerPacket(SetHealthPacket.class, SetHealthSerializer_v291.INSTANCE, 42)
            .registerPacket(SetSpawnPositionPacket.class, SetSpawnPositionSerializer_v407.INSTANCE, 43)
            .registerPacket(AnimatePacket.class, AnimateSerializer_v291.INSTANCE, 44)
            .registerPacket(RespawnPacket.class, RespawnSerializer_v388.INSTANCE, 45)
            .registerPacket(ContainerOpenPacket.class, ContainerOpenSerializer_v291.INSTANCE, 46)
            .registerPacket(ContainerClosePacket.class, ContainerCloseSerializer_v414.INSTANCE, 47)
            .registerPacket(PlayerHotbarPacket.class, PlayerHotbarSerializer_v291.INSTANCE, 48)
            .registerPacket(InventoryContentPacket.class, InventoryContentSerializer_v407.INSTANCE, 49)
            .registerPacket(InventorySlotPacket.class, InventorySlotSerializer_v407.INSTANCE, 50)
            .registerPacket(ContainerSetDataPacket.class, ContainerSetDataSerializer_v291.INSTANCE, 51)
            .registerPacket(CraftingDataPacket.class, CraftingDataSerializer_v407.INSTANCE, 52)
            .registerPacket(CraftingEventPacket.class, CraftingEventSerializer_v291.INSTANCE, 53)
            .registerPacket(GuiDataPickItemPacket.class, GuiDataPickItemSerializer_v291.INSTANCE, 54)
            .registerPacket(AdventureSettingsPacket.class, AdventureSettingsSerializer_v291.INSTANCE, 55)
            .registerPacket(BlockEntityDataPacket.class, BlockEntityDataSerializer_v291.INSTANCE, 56)
            .registerPacket(PlayerInputPacket.class, PlayerInputSerializer_v291.INSTANCE, 57)
            .registerPacket(LevelChunkPacket.class, LevelChunkSerializer_v361.INSTANCE, 58)
            .registerPacket(SetCommandsEnabledPacket.class, SetCommandsEnabledSerializer_v291.INSTANCE, 59)
            .registerPacket(SetDifficultyPacket.class, SetDifficultySerializer_v291.INSTANCE, 60)
            .registerPacket(ChangeDimensionPacket.class, ChangeDimensionSerializer_v291.INSTANCE, 61)
            .registerPacket(SetPlayerGameTypePacket.class, SetPlayerGameTypeSerializer_v291.INSTANCE, 62)
            .registerPacket(PlayerListPacket.class, PlayerListSerializer_v390.INSTANCE, 63)
            .registerPacket(SimpleEventPacket.class, SimpleEventSerializer_v291.INSTANCE, 64)
            .registerPacket(EventPacket.class, EventSerializer_v388.INSTANCE, 65)
            .registerPacket(SpawnExperienceOrbPacket.class, SpawnExperienceOrbSerializer_v291.INSTANCE, 66)
            .registerPacket(ClientboundMapItemDataPacket.class, ClientboundMapItemDataSerializer_v354.INSTANCE, 67)
            .registerPacket(MapInfoRequestPacket.class, MapInfoRequestSerializer_v291.INSTANCE, 68)
            .registerPacket(RequestChunkRadiusPacket.class, RequestChunkRadiusSerializer_v291.INSTANCE, 69)
            .registerPacket(ChunkRadiusUpdatedPacket.class, ChunkRadiusUpdatedSerializer_v291.INSTANCE, 70)
            .registerPacket(ItemFrameDropItemPacket.class, ItemFrameDropItemSerializer_v291.INSTANCE, 71)
            .registerPacket(GameRulesChangedPacket.class, GameRulesChangedSerializer_v291.INSTANCE, 72)
            .registerPacket(CameraPacket.class, CameraSerializer_v291.INSTANCE, 73)
            .registerPacket(BossEventPacket.class, BossEventSerializer_v291.INSTANCE, 74)
            .registerPacket(ShowCreditsPacket.class, ShowCreditsSerializer_v291.INSTANCE, 75)
            .registerPacket(AvailableCommandsPacket.class, AvailableCommandsSerializer_v388.INSTANCE, 76)
            .registerPacket(CommandRequestPacket.class, CommandRequestSerializer_v291.INSTANCE, 77)
            .registerPacket(CommandBlockUpdatePacket.class, CommandBlockUpdateSerializer_v361.INSTANCE, 78)
            .registerPacket(CommandOutputPacket.class, CommandOutputSerializer_v291.INSTANCE, 79)
            .registerPacket(UpdateTradePacket.class, UpdateTradeSerializer_v354.INSTANCE, 80)
            .registerPacket(UpdateEquipPacket.class, UpdateEquipSerializer_v291.INSTANCE, 81)
            .registerPacket(ResourcePackDataInfoPacket.class, ResourcePackDataInfoSerializer_v361.INSTANCE, 82)
            .registerPacket(ResourcePackChunkDataPacket.class, ResourcePackChunkDataSerializer_v388.INSTANCE, 83)
            .registerPacket(ResourcePackChunkRequestPacket.class, ResourcePackChunkRequestSerializer_v291.INSTANCE, 84)
            .registerPacket(TransferPacket.class, TransferSerializer_v291.INSTANCE, 85)
            .registerPacket(PlaySoundPacket.class, PlaySoundSerializer_v291.INSTANCE, 86)
            .registerPacket(StopSoundPacket.class, StopSoundSerializer_v291.INSTANCE, 87)
            .registerPacket(SetTitlePacket.class, SetTitleSerializer_v291.INSTANCE, 88)
            .registerPacket(AddBehaviorTreePacket.class, AddBehaviorTreeSerializer_v291.INSTANCE, 89)
            .registerPacket(StructureBlockUpdatePacket.class, StructureBlockUpdateSerializer_v388.INSTANCE, 90)
            .registerPacket(ShowStoreOfferPacket.class, ShowStoreOfferSerializer_v291.INSTANCE, 91)
            .registerPacket(PurchaseReceiptPacket.class, PurchaseReceiptSerializer_v291.INSTANCE, 92)
            .registerPacket(PlayerSkinPacket.class, PlayerSkinSerializer_v390.INSTANCE, 93)
            .registerPacket(SubClientLoginPacket.class, SubClientLoginSerializer_v291.INSTANCE, 94)
            .registerPacket(AutomationClientConnectPacket.class, AutomationClientConnectSerializer_v291.INSTANCE, 95)
            .registerPacket(SetLastHurtByPacket.class, SetLastHurtBySerializer_v291.INSTANCE, 96)
            .registerPacket(BookEditPacket.class, BookEditSerializer_v291.INSTANCE, 97)
            .registerPacket(NpcRequestPacket.class, NpcRequestSerializer_v291.INSTANCE, 98)
            .registerPacket(PhotoTransferPacket.class, PhotoTransferSerializer_v291.INSTANCE, 99)
            .registerPacket(ModalFormRequestPacket.class, ModalFormRequestSerializer_v291.INSTANCE, 100)
            .registerPacket(ModalFormResponsePacket.class, ModalFormResponseSerializer_v291.INSTANCE, 101)
            .registerPacket(ServerSettingsRequestPacket.class, ServerSettingsRequestSerializer_v291.INSTANCE, 102)
            .registerPacket(ServerSettingsResponsePacket.class, ServerSettingsResponseSerializer_v291.INSTANCE, 103)
            .registerPacket(ShowProfilePacket.class, ShowProfileSerializer_v291.INSTANCE, 104)
            .registerPacket(SetDefaultGameTypePacket.class, SetDefaultGameTypeSerializer_v291.INSTANCE, 105)
            .registerPacket(RemoveObjectivePacket.class, RemoveObjectiveSerializer_v291.INSTANCE, 106)
            .registerPacket(SetDisplayObjectivePacket.class, SetDisplayObjectiveSerializer_v291.INSTANCE, 107)
            .registerPacket(SetScorePacket.class, SetScoreSerializer_v291.INSTANCE, 108)
            .registerPacket(LabTablePacket.class, LabTableSerializer_v291.INSTANCE, 109)
            .registerPacket(UpdateBlockSyncedPacket.class, UpdateBlockSyncedSerializer_v291.INSTANCE, 110)
            .registerPacket(MoveEntityDeltaPacket.class, MoveEntityDeltaSerializer_v409.INSTANCE, 111)
            .registerPacket(SetScoreboardIdentityPacket.class, SetScoreboardIdentitySerializer_v291.INSTANCE, 112)
            .registerPacket(SetLocalPlayerAsInitializedPacket.class, SetLocalPlayerAsInitializedSerializer_v291.INSTANCE, 113)
            .registerPacket(UpdateSoftEnumPacket.class, UpdateSoftEnumSerializer_v291.INSTANCE, 114)
            .registerPacket(NetworkStackLatencyPacket.class, NetworkStackLatencySerializer_v332.INSTANCE, 115)
            .registerPacket(ScriptCustomEventPacket.class, ScriptCustomEventSerializer_v291.INSTANCE, 117)
            .registerPacket(SpawnParticleEffectPacket.class, SpawnParticleEffectSerializer_v332.INSTANCE, 118)
            .registerPacket(AvailableEntityIdentifiersPacket.class, AvailableEntityIdentifiersSerializer_v313.INSTANCE, 119)
            .registerPacket(LevelSoundEvent2Packet.class, LevelSoundEvent2Serializer_v407.INSTANCE, 120)
            .registerPacket(NetworkChunkPublisherUpdatePacket.class, NetworkChunkPublisherUpdateSerializer_v313.INSTANCE, 121)
            .registerPacket(BiomeDefinitionListPacket.class, BiomeDefinitionListSerializer_v313.INSTANCE, 122)
            .registerPacket(LevelSoundEventPacket.class, LevelSoundEventSerializer_v407.INSTANCE, 123)
            .registerPacket(LevelEventGenericPacket.class, LevelEventGenericSerializer_v361.INSTANCE, 124)
            .registerPacket(LecternUpdatePacket.class, LecternUpdateSerializer_v354.INSTANCE, 125)
            .registerPacket(VideoStreamConnectPacket.class, VideoStreamConnectSerializer_v361.INSTANCE, 126)
            // AddEntityPacket 127
            // RemoveEntityPacket 128
            .registerPacket(ClientCacheStatusPacket.class, ClientCacheStatusSerializer_v361.INSTANCE, 129)
            .registerPacket(OnScreenTextureAnimationPacket.class, OnScreenTextureAnimationSerializer_v354.INSTANCE, 130)
            .registerPacket(MapCreateLockedCopyPacket.class, MapCreateLockedCopySerializer_v354.INSTANCE, 131)
            .registerPacket(StructureTemplateDataRequestPacket.class, StructureTemplateDataRequestSerializer_v361.INSTANCE, 132)
            .registerPacket(StructureTemplateDataResponsePacket.class, StructureTemplateDataResponseSerializer_v388.INSTANCE, 133)
            .registerPacket(UpdateBlockPropertiesPacket.class, UpdateBlockPropertiesSerializer_v361.INSTANCE, 134)
            .registerPacket(ClientCacheBlobStatusPacket.class, ClientCacheBlobStatusSerializer_v361.INSTANCE, 135)
            .registerPacket(ClientCacheMissResponsePacket.class, ClientCacheMissResponseSerializer_v361.INSTANCE, 136)
            .registerPacket(EducationSettingsPacket.class, EducationSettingsSerializer_v388.INSTANCE, 137)
            .registerPacket(EmotePacket.class, EmoteSerializer_v388.INSTANCE, 138)
            .registerPacket(MultiplayerSettingsPacket.class, MultiplayerSettingsSerializer_v388.INSTANCE, 139)
            .registerPacket(SettingsCommandPacket.class, SettingsCommandSerializer_v388.INSTANCE, 140)
            .registerPacket(AnvilDamagePacket.class, AnvilDamageSerializer_v388.INSTANCE, 141)
            .registerPacket(CompletedUsingItemPacket.class, CompletedUsingItemSerializer_v388.INSTANCE, 142)
            .registerPacket(NetworkSettingsPacket.class, NetworkSettingsSerializer_v388.INSTANCE, 143)
            .registerPacket(PlayerAuthInputPacket.class, PlayerAuthInputSerializer_v414.INSTANCE, 144)
            .registerPacket(CreativeContentPacket.class, CreativeContentSerializer_v407.INSTANCE, 145)
            .registerPacket(PlayerEnchantOptionsPacket.class, PlayerEnchantOptionsSerializer_v407.INSTANCE, 146)
            .registerPacket(ItemStackRequestPacket.class, ItemStackRequestSerializer_v407.INSTANCE, 147)
            .registerPacket(ItemStackResponsePacket.class, ItemStackResponseSerializer_v414.INSTANCE, 148)
            .registerPacket(PlayerArmorDamagePacket.class, PlayerArmorDamageSerializer_v407.INSTANCE, 149)
            .registerPacket(CodeBuilderPacket.class, CodeBuilderSerializer_v407.INSTANCE, 150)
            .registerPacket(UpdatePlayerGameTypePacket.class, UpdatePlayerGameTypeSerializer_v407.INSTANCE, 151)
            .registerPacket(EmoteListPacket.class, EmoteListSerializer_v407.INSTANCE, 152)
            .registerPacket(PositionTrackingDBServerBroadcastPacket.class, PositionTrackingDBServerBroadcastSerializer_v407.INSTANCE, 153)
            .registerPacket(PositionTrackingDBClientRequestPacket.class, PositionTrackingDBClientRequestSerializer_v407.INSTANCE, 154)
            .registerPacket(DebugInfoPacket.class, DebugInfoSerializer_v407.INSTANCE, 155)
            .registerPacket(PacketViolationWarningPacket.class, PacketViolationWarningSerializer_v407.INSTANCE, 156)
            .build();
}
